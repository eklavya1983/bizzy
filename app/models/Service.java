package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2/4/15.
 */
@Entity
@NamedQuery(name = "Service.find", query = "SELECT s FROM Service AS s WHERE s.name = :name")
public class Service {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String name;
    @ManyToMany
    private List<Business> businesses = new ArrayList<Business>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }
}
