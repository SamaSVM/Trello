package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.domain.perent.Resource;

@Data
public class Workspace extends Resource {
    private String name;
    private String description;
    private WorkspaceVisibility visibility = WorkspaceVisibility.PRIVATE;

    @Override
    public String toString() {
        return "Workspace{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", visibility=" + visibility +
                '}';
    }
}
