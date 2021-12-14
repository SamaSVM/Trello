package spd.trello.domain;

import lombok.Data;

@Data
public class Member extends Resource {
    private User user;
    private MemberRole memberRole = MemberRole.GUEST;

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", user=" + user +
                ", memberRole=" + memberRole +
                '}';
    }
}
