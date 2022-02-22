package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Board;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.BoardRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BoardService extends AbstractService<Board, BoardRepository> {
    public BoardService(BoardRepository repository, CardListService cardListService) {
        super(repository);
        this.cardListService = cardListService;
    }

    private final CardListService cardListService;

    @Override
    public Board save(Board entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Board update(Board entity) {
        Board oldBoard = getById(entity.getId());

        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("Not found updated by!");
        }

        if (entity.getName() == null && entity.getDescription() == null
                && entity.getFavourite() == oldBoard.getFavourite() && entity.getArchived() == oldBoard.getArchived()
                && entity.getMembersId().equals(oldBoard.getMembersId())
                && entity.getVisibility().equals(oldBoard.getVisibility())) {
            throw new ResourceNotFoundException();
        }

        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldBoard.getCreatedBy());
        entity.setCreatedDate(oldBoard.getCreatedDate());
        entity.setWorkspaceId(oldBoard.getWorkspaceId());
        if (entity.getName() == null) {
            entity.setName(oldBoard.getName());
        }
        if (entity.getDescription() == null && oldBoard.getDescription() != null) {
            entity.setDescription(oldBoard.getDescription());
        }
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        cardListService.deleteCardListsForBoard(id);
        super.delete(id);
    }

    public void deleteBoardForWorkspace(UUID workspaceId) {
        repository.findAllByWorkspaceId(workspaceId).forEach(board -> delete(board.getId()));
    }

    public void deleteMemberInBoards(UUID memberId) {
        List<Board> boards = repository.findAllBymembersIdEquals(memberId);
        for (Board board : boards) {
            Set<UUID> membersId = board.getMembersId();
            membersId.remove(memberId);
            if (board.getMembersId().isEmpty()) {
                delete(board.getId());
            }
        }
    }
}
