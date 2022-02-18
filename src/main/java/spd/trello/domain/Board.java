package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.domain.perent.Resource;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "boards")
public class Board extends Resource {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private BoardVisibility visibility = BoardVisibility.PRIVATE;

    @Column(name = "favourite")
    private Boolean favourite = false;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "workspace_id")
    private UUID workspaceId;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
            name = "member_board",
            joinColumns=@JoinColumn(name= "board_id")
    )
    @Column(name = "member_id")
    private Set<UUID> membersIds = new HashSet<>();
}
