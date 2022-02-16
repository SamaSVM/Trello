package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
public class User extends Domain {
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "time_zone")
    private String timeZone;

    @Override
    public String toString() {
        return "User{" +
                "id=" + super.getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", timeZone=" + timeZone +
                '}';
    }
}
