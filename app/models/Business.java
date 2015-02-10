package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2/4/15.
 */
@Entity
@NamedQuery(name = "Business.find", query = "SELECT b FROM Business AS b WHERE b.name = :name")
// TODO(Rao): Add constraints
public class Business {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String name;
    private String address;
    private String desc;
    private String services;

    public final static String DELIM = ";";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getServices() {
        return services;
    }

    @Override
    public boolean equals(Object obj) {
        Business b = (Business) obj;
        return  (id == b.id &&
                name.equals(b.getName()) &&
                address.equals(b.getAddress()) &&
                desc.equals(b.getDesc()) &&
                services.equals(b.getServices()));
    }

    public void setServices(String services) {
        this.services = services;
    }
}
