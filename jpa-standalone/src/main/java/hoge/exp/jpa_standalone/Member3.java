package hoge.exp.jpa_standalone;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "member")
@NamedQuery(name = "Member3.findAll", query = "SELECT m FROM Member3 m")
public class Member3 {
    @Id
    private long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;

    public Member3() {}

    public Member3(long id, String name, String email, LocalDate dateOfBirth) {
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
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
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
