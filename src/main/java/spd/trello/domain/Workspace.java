package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "workspaces")
public class Workspace extends Resource {
    @Column(name = "name")
    @NotNull(message = "The name field must be filled.")
    @Size(min = 2, max = 20, message = "The name field must be between 2 and 20 characters long.")
    private String name;

    @Column(name = "description")
    @Size(min = 2, max = 255, message = "The description field must be between 2 and 255 characters long.")
    private String description;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private WorkspaceVisibility visibility = WorkspaceVisibility.PRIVATE;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "member_workspace",
            joinColumns = @JoinColumn(name = "workspace_id")
    )
    @Column(name = "member_id")
    private Set<UUID> membersId = new HashSet<>();
}
