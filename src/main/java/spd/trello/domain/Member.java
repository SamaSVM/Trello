package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.enums.Role;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "members")
public class Member extends Resource {
    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private Role memberRole = Role.GUEST;

    @Column(name = "user_id")
    private UUID userId;
}


