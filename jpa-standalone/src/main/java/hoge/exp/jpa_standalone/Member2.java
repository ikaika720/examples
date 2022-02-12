package hoge.exp.jpa_standalone;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class Member2 {
    @Id
    private long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
    		name = "member_member", 
    		joinColumns = @JoinColumn(name = "member_id"), 
    		inverseJoinColumns = @JoinColumn(name = "friends_id"))
    private Collection<Member2> friends;

    public Member2() {}

    public Member2(long id, String name, String email, LocalDate dateOfBirth) {
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
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
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
