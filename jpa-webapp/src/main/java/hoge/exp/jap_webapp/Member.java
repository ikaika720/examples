package hoge.exp.jap_webapp;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Member {
    @Id
    private long id;
    private String name;
    private String email;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfBirth;

    public Member() {}

    public Member(long id, String name, String email, Date dateOfBirth) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
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

    @Override
    public String toString() {
        return "Member [id=" + id +
                ", name=\"" + name +
                "\", email=\"" + email +
                "\", deteOfBirth=\"" + dateOfBirth + "\"]";
    }
}
