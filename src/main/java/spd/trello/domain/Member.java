package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.enums.MemberRole;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "members")
public class Member extends Resource {
    @Column(name = "member_role")
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole = MemberRole.GUEST;

    @Column(name = "user_id")
    private UUID userId;
}


