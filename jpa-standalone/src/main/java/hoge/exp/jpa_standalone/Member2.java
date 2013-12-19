package hoge.exp.jpa_standalone;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "member")
public class Member2 {
    @Id
    private long id;
    private String name;
    private String email;
    private Date dateOfBirth;
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Member2> friends;

    public Member2() {}

    public Member2(long id, String name, String email, Date dateOfBirth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.friends = new ArrayList<Member2>();
    }

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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Collection<Member2> getFriends() {
        return friends;
    }

    public void setFriends(Collection<Member2> friends) {
        this.friends = friends;
    }

    @Override
    public String toString() {
        // friends is not contained not to load the records.
        return "Member [id=" + id +
                ", name=\"" + name +
                "\", email=\"" + email +
                "\", deteOfBirth=\"" + dateOfBirth + "\"]";
    }
}
