package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.enums.MemberRole;
import spd.trello.domain.perent.Resource;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
public class Member extends Resource {
    private UUID userId;
    private MemberRole memberRole = MemberRole.GUEST;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", userId=" + userId +
                ", memberRole=" + memberRole +
                '}';
    }
}
