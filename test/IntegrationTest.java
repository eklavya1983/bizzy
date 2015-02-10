import com.fasterxml.jackson.databind.JsonNode;
import models.Business;
import models.User;
import org.junit.Assert;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

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
//        /* Add a business with two services */
//        for (String s : inMemBizzy.getServiceToBusinessTbl().keySet()) {
//            WSResponse wsResponse = WS.url("http://localhost:3333/search")
//                    .put(userNode)
//                    .get(TestUtils.REQUEST_TIMEOUT_MS);
//
//            Assert.assertEquals(200, wsResponse.getStatus());
//
//        }
    }

}
