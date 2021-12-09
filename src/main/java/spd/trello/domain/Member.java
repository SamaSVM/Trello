package spd.trello.domain;

import lombok.Data;

@Data
public class Member {
    private User user;
    private MemberRole memberRole = MemberRole.GUEST;
}
