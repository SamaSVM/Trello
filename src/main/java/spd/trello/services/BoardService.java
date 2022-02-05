package spd.trello.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.Board;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BoardService extends AbstractService<Board> {
    public BoardService(InterfaceRepository<Board> repository, MemberBoardService memberBoardService, BoardWorkspaceService boardWorkspaceService) {
        super(repository);
        this.memberBoardService = memberBoardService;
        this.boardWorkspaceService = boardWorkspaceService;
    }

    private final MemberBoardService memberBoardService;

    private final BoardWorkspaceService boardWorkspaceService;

    @Override
    public Board create(Board entity) {
        Board board = new Board();
        board.setId(UUID.randomUUID());
        board.setCreatedBy(entity.getCreatedBy());
        board.setCreatedDate(Date.valueOf(LocalDate.now()));
        board.setName(entity.getName());
        if (entity.getDescription() != null) {
            board.setDescription(entity.getDescription());
        }
        board.setVisibility(entity.getVisibility());
        board.setWorkspaceId(entity.getWorkspaceId());
        repository.create(board);
        return repository.findById(board.getId());
    }

    @Override
    public Board update(Board entity) {
        Board oldBoard = findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldBoard.getName());
        }
        if (entity.getDescription() == null) {
            entity.setDescription(oldBoard.getDescription());
        }
        if (entity.getVisibility() == null) {
            entity.setVisibility(oldBoard.getVisibility());
        }
        if (entity.getFavourite() == null) {
            entity.setFavourite(oldBoard.getFavourite());
        }
        if (entity.getArchived() == null) {
            entity.setArchived(oldBoard.getArchived());
        }
        return repository.update(entity);
    }

    public boolean addMember(UUID newMemberId, UUID boardId) {
        return memberBoardService.create(newMemberId, boardId);
    }

    public boolean deleteMember(UUID memberId, UUID boardId) {
        return memberBoardService.delete(memberId, boardId);
    }

    public List<Board> getAllBoardsForWorkspace( UUID workspaceId) {
        return boardWorkspaceService.getAllBoardsForWorkspace(workspaceId);
    }
}
