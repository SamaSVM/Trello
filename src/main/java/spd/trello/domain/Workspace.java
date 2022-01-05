package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.domain.perent.Resource;

import java.util.ArrayList;
import java.util.List;

@Data
public class Workspace extends Resource {
    private String name;
    private List<Board> boards;
    private List<Member> members = new ArrayList<>();
    private String description;
    private WorkspaceVisibility visibility = WorkspaceVisibility.PRIVATE;

    @Override
    public String toString() {
        return "Workspace{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCratedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", boards=" + boards +
                ", members=" + members +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                '}';
    }
}
