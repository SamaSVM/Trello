package spd.trello.services;

import spd.trello.ConnectionPool;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;
import spd.trello.repository.MemberBoardRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BoardService extends AbstractService<Board> {
    public BoardService(InterfaceRepository<Board> repository) {
        super(repository);
    }

    private final MemberBoardService MBService =
            new MemberBoardService(new MemberBoardRepository(ConnectionPool.createDataSource()));

    public Board findById(UUID id) {
        return repository.findById(id);
    }

    public List<Board> findAll() {
        return repository.findAll();
    }

    public Board create(Member member, UUID workspaceId, String name, String description) {
        Board board = new Board();
        board.setId(UUID.randomUUID());
        board.setCreatedBy(member.getCreatedBy());
        board.setCreatedDate(Date.valueOf(LocalDate.now()));
        board.setName(name);
        board.getMembers().add(member);
        if (description != null) {
            board.setDescription(description);
        }
        board.setWorkspaceId(workspaceId);
        repository.create(board);
        if(!MBService.create(member.getId(), board.getId())){
            delete(board.getId());
        }
        return repository.findById(board.getId());
    }

    public Board update(Member member, Board entity) {
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This user cannot update workspace!");
        }
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        MBService.delete(id);
        return repository.delete(id);
    }
}
