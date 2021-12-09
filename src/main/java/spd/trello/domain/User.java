package spd.trello.domain;

import lombok.Data;

import java.util.TimeZone;

@Data
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private TimeZone timeZone;
}
