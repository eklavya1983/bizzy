package models;

import javax.persistence.*;

/**
 * Created on 2/11/15.
 */
@Entity
@NamedQuery(name = "Booking.findByUser", query = "SELECT b FROM Booking b JOIN b.user u WHERE u.id = :userId")
public class Booking {
    enum BookingState {USER_INITIATED, BUSINESS_ACCEPTED, COMPLETE, CANCELLED};

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Business business;
    private BookingState state;
    private long startTs;
    private long endTs;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business business) {
        this.business = business;
    }

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public long getStartTs() {
        return startTs;
    }

    public void setStartTs(long startTs) {
        this.startTs = startTs;
    }

    public long getEndTs() {
        return endTs;
    }

    public void setEndTs(long endTs) {
        this.endTs = endTs;
    }

    @Override
    public boolean equals(Object obj) {
        Booking b = (Booking) obj;
        return (this.getId() == b.getId() &&
                this.getUser().getId() == b.getUser().getId() &&
                this.getBusiness().getId() == this.getBusiness().getId());
    }
}
