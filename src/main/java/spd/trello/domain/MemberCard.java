package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class MemberCard {
    private UUID memberId;
    private UUID cardId;
}
