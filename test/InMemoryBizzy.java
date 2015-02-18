import models.Booking;
import models.Business;
import models.User;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 2/10/15.
 */
public class InMemoryBizzy {
    static Comparator<Business> businessComparator = (b1, b2) -> Long.valueOf(b1.getId()).compareTo(b2.getId());

    private Hashtable<Long, User>  userTbl = new Hashtable<>();
    private Hashtable<Long, Business>  businessTbl = new Hashtable<>();
    private Hashtable<Long, Booking>  bookingTbl = new Hashtable<>();
    private Hashtable<String, Set<Business>>  serviceToBusinessTbl = new Hashtable<>();

    public void addUser(User u) {
        userTbl.put(u.getId(), u);
    }

    public void addBusiness(Business b) {
        businessTbl.put(b.getId(), b);
        for (String s : b.getServices().split(Business.DELIM)) {
            if (!serviceToBusinessTbl.containsKey(s)) {
                serviceToBusinessTbl.put(s, new TreeSet<Business>(InMemoryBizzy.businessComparator));
            }
            serviceToBusinessTbl.get(s).add(b);
        }
    }

    public void addBooking(Booking b) {
        bookingTbl.put(b.getId(), b);
    }

    public Hashtable<Long, User> getUserTbl() {
        return userTbl;
    }

    public Hashtable<Long, Business> getBusinessTbl() {
        return businessTbl;
    }

    public Hashtable<String, Set<Business>> getServiceToBusinessTbl() {
        return serviceToBusinessTbl;
    }

    public Set<Business> getBusinesses(String s) {
        return serviceToBusinessTbl.get(s);
    }

    public User getRandomUser() {
        int randIdx = new Random().nextInt(userTbl.size());
        Enumeration<Long> keys = userTbl.keys();
        for (int i = 0;i < randIdx; i++) {
            keys.nextElement();
        }
        return userTbl.get(keys.nextElement());
    }

    public Business getRandomBusiness() {
        int randIdx = new Random().nextInt(businessTbl.size());
        Enumeration<Long> keys = businessTbl.keys();
        for (int i = 0;i < randIdx; i++) {
            keys.nextElement();
        }
        return businessTbl.get(keys.nextElement());
    }

    public List<Booking> getBookingsForUser(Long uid) {
        List<Booking> bookings = bookingTbl.keySet()
                .stream()
                .filter(k -> bookingTbl.get(k).getUser().getId() == uid)
                .map(k -> bookingTbl.get(k))
                .collect(Collectors.toList());
        return bookings;
    }
}
