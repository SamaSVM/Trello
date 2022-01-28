package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class MemberBoard {
    private UUID memberId;
    private UUID boardId;
}
