package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Workspace;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkspaceRepository extends AbstractRepository<Workspace> {
    List<Workspace> findAllBymembersIdEquals(UUID memberId);
}
