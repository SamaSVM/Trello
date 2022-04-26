package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "workspaces")
public class Workspace extends Resource {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
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
