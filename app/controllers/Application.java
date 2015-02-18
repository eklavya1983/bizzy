package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Booking;
import models.Business;
import models.Service;
import models.User;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import views.html.index;

import javax.persistence.TypedQuery;
import java.util.List;

public class Application extends Controller {
    public final static String USERID = "userId";
    public final static String BUSINESSID = "businessId";
    public final static String SERVICE_NAME = "serviceName";

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    @Transactional
    public static Result addBusiness() {
        Http.RequestBody body = request().body();
        JsonNode bodyNode = body.asJson();
        Business business = Json.fromJson(bodyNode, Business.class);
        JPA.em().persist(business);
        Logger.info("Persisted businsess: " + business.getName());

        for (String serviceName : business.getServices().split(Business.DELIM)) {
            TypedQuery<Service> query = JPA.em().createNamedQuery("Service.find", Service.class);
            query.setParameter("name", serviceName);
            List<Service> resultList = query.getResultList();
            Service service;
            if (resultList == null || resultList.size() == 0) {
                Logger.info("New service: " + serviceName + ".  Adding businsess: " + business.getName());
                service = new Service();
                service.setName(serviceName);
                service.getBusinesses().add(business);
                JPA.em().persist(service);
            } else {
                Logger.info("Existing service: " + serviceName + ".  Adding businsess: " + business.getName());
                service = resultList.get(0);
                service.getBusinesses().add(business);
            }
        }
        return ok(Json.toJson(business.getId()));
    }

    @Transactional(readOnly = true)
    public static Result getBusiness(long id) {
        Business b = JPA.em().find(Business.class, id);
        if (b == null) {
            Logger.info("Business with Id: " + id + " not found");
            return Results.noContent();
        }
        return Results.ok(Json.toJson(b));
    }

    @Transactional
    public static Result addUser() {
        Http.RequestBody body = request().body();
        JsonNode bodyNode = body.asJson();
        User user = Json.fromJson(bodyNode, User.class);
        JPA.em().persist(user);
        Logger.info("Persisted user: " + user.getName());
        return Results.ok(Json.toJson(user.getId()));
    }

    @Transactional(readOnly = true)
    public static Result getUser(long id) {
        User u = JPA.em().find(User.class, id);
        if (u == null) {
            Logger.info("User with Id: " + id + " not found");
            return Results.noContent();
        }
        return Results.ok(Json.toJson(u));
    }

    /**
     * Searches for services.
     * For now only supports searching by a given service name
     * @return
     */
    @Transactional(readOnly = true)
    public static Result search() {
        // TODO(Rao): We need to make this more refined.  We need to support
        // search criteria multiple filters
        String serviceName = request().getQueryString(SERVICE_NAME );
        TypedQuery<Service> query = JPA.em().createNamedQuery("Service.find", Service.class);
        query.setParameter("name", serviceName);
        List<Service> resultList = query.getResultList();
        Service service;
        List<Business> businessList = null;
        if (resultList != null && resultList.size() != 0) {
            businessList = resultList.get(0).getBusinesses();
        }
        return Results.ok(Json.toJson(businessList));
    }

    /**
     * Adds booking between a user and business
     * @return
     */
    @Transactional
    public static Result addBooking() {
        /* Parse json body */
        JsonNode jsonNode = request().body().asJson();
        Booking booking = Json.fromJson(jsonNode, Booking.class);

        /* Get the business and user entities based on the passed in Ids */
        Business business =  JPA.em().find(Business.class, booking.getBusiness().getId());
        User user =  JPA.em().find(User.class, booking.getUser().getId());

        /* Replace passed user, business with JPA entities */
        booking.setUser(user);
        booking.setBusiness(business);
        JPA.em().persist(booking);

        return Results.ok(Json.toJson(booking.getId()));
    }

    /**
     * Returns booking information based on booking id
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public static Result getBooking(long id) {
        Booking booking = JPA.em().find(Booking.class, id);
        return Results.ok(Json.toJson(booking));
    }

    /**
     *
     * @return bookings for user identified by user id
     */
    @Transactional(readOnly = true)
    public static Result getBookingsForUser() {
        long userId = Long.parseLong(request().getQueryString(USERID));
        TypedQuery<Booking> query = JPA.em().createNamedQuery("Booking.findByUser", Booking.class);
        query.setParameter("userId", userId);
        List<Booking> bookingList = query.getResultList();
        return Results.ok(Json.toJson(bookingList));
    }
}
