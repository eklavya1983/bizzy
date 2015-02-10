package models;

import javax.persistence.*;

/**
 * Created on 2/4/15.
 */
@Entity
// TODO(Rao): Add constraints
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private String name;
    private String address;


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

    @Override
    public boolean equals(Object obj) {
        User b = (User) obj;
        return  (id == b.id &&
                name.equals(b.getName()) &&
                address.equals(b.getAddress()));
    }
}
