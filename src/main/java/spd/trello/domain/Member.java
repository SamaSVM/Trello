package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.enums.MemberRole;
import spd.trello.domain.perent.Resource;

@Data
public class Member extends Resource {
    private User user;
    private MemberRole memberRole = MemberRole.GUEST;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCratedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", user=" + user +
                ", memberRole=" + memberRole +
                '}';
    }
}
