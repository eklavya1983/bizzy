import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.Application;
import models.Booking;
import models.Business;
import models.User;
import org.junit.Assert;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static play.test.Helpers.*;

public class IntegrationTest {
    TestUtils.BusinessSupplier businessSupplier = new TestUtils.BusinessSupplier();
    TestUtils.UserSupplier userSupplier = new TestUtils.UserSupplier();
    InMemoryBizzy inMemBizzy = new InMemoryBizzy();

    @Test
    public void testInServer() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            System.out.println("Started");
            /* Test adding business and getting business */
            testAddBusiness();
            /* Test adding user and getting user */
            testAddUser();
            /* Test searching service */
            testSearch();

            /* test booking */
            testBooking();

            /* Test getBookingForUser for all the users */
            testGetBookings();
        });
    }

    void testAddBusiness() {
        Business b = new TestUtils.BusinessSupplier().get();
        JsonNode busNode = Json.toJson(b);

        /* Add business */
        WSResponse wsResponse = WS.url("http://localhost:3333/business")
                .put(busNode)
                .get(TestUtils.REQUEST_TIMEOUT_MS);

        /* Make sure business is added */
        Assert.assertEquals(200, wsResponse.getStatus());

        /* Add to local in memory bizzy for another test */
        JsonNode response = wsResponse.asJson();
        b.setId(response.asLong());
        inMemBizzy.addBusiness(b);

        /* Do a get on business */
        wsResponse = WS.url("http://localhost:3333/business/" + b.getId())
                .get()
                .get(TestUtils.REQUEST_TIMEOUT_MS);

        /* Get should succeed */
        Assert.assertEquals(200, wsResponse.getStatus());
        Business retBus = Json.fromJson(wsResponse.asJson(), Business.class);
        Assert.assertTrue(retBus.equals(b));
    }

    void testAddUser() {
        User u = new TestUtils.UserSupplier().get();
        JsonNode userNode = Json.toJson(u);

        WSResponse wsResponse = WS.url("http://localhost:3333/user")
                .put(userNode)
                .get(TestUtils.REQUEST_TIMEOUT_MS);

        Assert.assertEquals(200, wsResponse.getStatus());

        JsonNode response = wsResponse.asJson();
        u.setId(response.asLong());
        inMemBizzy.addUser(u);

        wsResponse = WS.url("http://localhost:3333/user/" + u.getId())
                .get()
                .get(TestUtils.REQUEST_TIMEOUT_MS);

        Assert.assertEquals(200, wsResponse.getStatus());
        User retUser = Json.fromJson(wsResponse.asJson(), User.class);
        Assert.assertTrue(retUser.equals(u));
    }

    void testSearch() {
        /* Add a business with two services */
        for (String s : inMemBizzy.getServiceToBusinessTbl().keySet())
            try {
                /* Search for businesses matching service s */
                WSResponse wsResponse = WS.url("http://localhost:3333/search")
                        .setQueryParameter("serviceName", s)
                        .get()
                        .get(TestUtils.REQUEST_TIMEOUT_MS);

                Assert.assertEquals(200, wsResponse.getStatus());

                /* Parse out business list */
                List<Business> businessList = null;
                try {
                    businessList = new ObjectMapper().readValue(wsResponse.asByteArray(), new TypeReference<List<Business>>() {});
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /* Make sure every business exists */
                Set<Business> actualSet = businessList.stream()
                        .collect(Collectors.toCollection(() -> new TreeSet<Business>(InMemoryBizzy.businessComparator)));
                Set<Business> expectedSet = inMemBizzy.getBusinesses(s);
                Assert.assertTrue(actualSet.equals(expectedSet));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    void testBooking() {
        /* Create random booking */
        Booking b = TestUtils.createBooking(inMemBizzy.getRandomUser(),
                inMemBizzy.getRandomBusiness());
        JsonNode bookingNode = Json.toJson(b);

        /* Add booking */
        WSResponse wsResponse = WS.url("http://localhost:3333/booking")
                .put(bookingNode)
                .get(TestUtils.REQUEST_TIMEOUT_MS);

        /* Make sure booking is added */
        Assert.assertEquals(200, wsResponse.getStatus());

        /* Add to local in memory bizzy for another test */
        JsonNode response = wsResponse.asJson();
        b.setId(response.asLong());
        inMemBizzy.addBooking(b);

        /* Do a get on booking */
        wsResponse = WS.url("http://localhost:3333/booking/" + b.getId())
                .get()
                .get(TestUtils.REQUEST_TIMEOUT_MS);

        /* Get should succeed */
        Assert.assertEquals(200, wsResponse.getStatus());
        Booking retBooking = Json.fromJson(wsResponse.asJson(), Booking.class);
        Assert.assertTrue(retBooking.equals(b));
    }

    void testGetBookings() {
        for (Long uid : inMemBizzy.getUserTbl().keySet()) {
            WSResponse wsResponse = WS.url("http://localhost:3333/search/bookings")
                    .setQueryParameter(Application.USERID, String.valueOf(uid))
                    .get()
                    .get(TestUtils.REQUEST_TIMEOUT_MS);
            /* Parse out booking list from the response */
            List<Booking> actualBookings = null;
            try {
                actualBookings = new ObjectMapper().readValue(wsResponse.asByteArray(), new TypeReference<List<Booking>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }

            /* Sort actual and expected */
            List<Booking> expBookings = inMemBizzy.getBookingsForUser(uid);
            actualBookings.sort((b1, b2) -> Long.valueOf(b1.getId()).compareTo(b2.getId()));
            expBookings.sort((b1, b2) -> Long.valueOf(b1.getId()).compareTo(b2.getId()));

            /* Compare */
            Assert.assertEquals(expBookings, actualBookings);
        }
    }

}
