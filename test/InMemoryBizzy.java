import models.Business;
import models.User;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created on 2/10/15.
 */
public class InMemoryBizzy {
    public void addUser(User u) {
        userTbl.put(u.getId(), u);
    }

    public void addBusiness(Business b) {
        businessTbl.put(b.getId(), b);
        for (String s : b.getServices().split(Business.DELIM)) {
            if (!serviceToBusinessTbl.containsKey(s)) {
                serviceToBusinessTbl.put(s, new HashSet<Business>());
            }
            serviceToBusinessTbl.get(s).add(b);
        }
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

    Hashtable<Long, User>  userTbl = new Hashtable<>();
    Hashtable<Long, Business>  businessTbl = new Hashtable<>();
    Hashtable<String, Set<Business>>  serviceToBusinessTbl = new Hashtable<>();


}
