package spd.trello.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;
import spd.trello.validators.annotation.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "users")
public class User extends Domain {
    @Column(name = "first_name")
    @NotNull(message = "The firstname field must be filled.")
    @Size(min = 2, max = 20, message = "The firstname field must be between 2 and 20 characters long.")
    private String firstName;

    @Column(name = "last_name")
    @NotNull(message = "The lastname field must be filled.")
    @Size(min = 2, max = 20, message = "The lastname field must be between 2 and 20 characters long.")
    private String lastName;

    @Column(name = "email")
    @Email(message = "The email field should look like email.")
    private String email;

    @Column(name = "time_zone")
    @NotNull(message = "The timezone field must be filled.")
    @TimeZone
    private String timeZone;
}
