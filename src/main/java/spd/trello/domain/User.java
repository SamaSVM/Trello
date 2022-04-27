package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import spd.trello.domain.enums.Role;
import spd.trello.domain.enums.UserStatus;
import spd.trello.domain.perent.Domain;

import javax.persistence.*;
import java.time.ZoneId;

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

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role = Role.GUEST;

    @Column(name = "time_zone")
    private String timeZone = ZoneId.systemDefault().toString();
}
