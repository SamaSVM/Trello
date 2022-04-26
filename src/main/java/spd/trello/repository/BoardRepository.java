package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Board;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends AbstractRepository<Board> {
    List<Board> findAllByMembersIdEquals(UUID memberId);

    List<Board> findAllByWorkspaceId(UUID workspaceId);
}