package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Board;
import spd.trello.repository.BoardRepository;
import spd.trello.validators.BoardValidator;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class BoardService extends AbstractService<Board, BoardRepository, BoardValidator> {
    public BoardService(BoardRepository repository, CardListService cardListService, BoardValidator validator) {
        super(repository, validator);
        this.cardListService = cardListService;
    }

    private final CardListService cardListService;

    @Override
    public void delete(UUID id) {
        cardListService.deleteCardListsForBoard(id);
        super.delete(id);
    }

    public void deleteBoardForWorkspace(UUID workspaceId) {
        repository.findAllByWorkspaceId(workspaceId).forEach(board -> delete(board.getId()));
    }

    public void deleteMemberInBoards(UUID memberId) {
        List<Board> boards = repository.findAllByMembersIdEquals(memberId);
        for (Board board : boards) {
            Set<UUID> membersId = board.getMembersId();
            membersId.remove(memberId);
            if (board.getMembersId().isEmpty()) {
                delete(board.getId());
            }
        }
    }
}
