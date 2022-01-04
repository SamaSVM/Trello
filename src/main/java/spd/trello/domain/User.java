package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Domain;


@Data
public class User extends Domain {
    private String firstName;
    private String lastName;
    private String email;
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
