import models.Booking;
import models.Business;
import models.User;

import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created on 2/5/15.
 */
public class TestUtils {
    final static int REQUEST_TIMEOUT_MS = 100000;
    static Random rand = new Random();
    static String services[] = {"mechanic", "plumber", "handyman"};

    static final class BusinessSupplier implements Supplier<Business> {
        @Override
        public Business get() {
            Business b = new Business();
            // TODO(Rao): make this random
            b.setName("business");
            b.setAddress("7072 N mariposa ct, dublin, ca");
            b.setDesc("We do plumbling");
            Set<String> serviceSet = /*rand.ints(rand.nextInt(2) + 1, 0, services.length)*/ IntStream.of(0, 1, 2)
                    .mapToObj(i -> services[i])
                    .collect(Collectors.toSet());
            String services = serviceSet.stream().collect(Collectors.joining(Business.DELIM));
            b.setServices(services);
            return b;
        }
    }

    static final class UserSupplier implements Supplier<User> {
        @Override
        public User get() {
            User u = new User();
            // TODO(Rao): make this random
            u.setName("user");
            u.setAddress("7072 N mariposa ct, dublin, ca");
            return u;
        }
    }

    static Booking createBooking(User user, Business business) {
        Booking b = new Booking();
        /* NOTE: We are setting both user and business.  All we really need to set are their
         *  ids
         */
        b.setUser(user);
        b.setBusiness(business);
        return b;
    }

}
