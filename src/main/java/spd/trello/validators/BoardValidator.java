package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Board;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.BoardRepository;

@Component
public class BoardValidator extends AbstractValidator<Board> {

    private final BoardRepository boardRepository;
    private final HelperValidator<Board> helper;

    public BoardValidator(BoardRepository boardRepository, HelperValidator<Board> helper) {
        this.boardRepository = boardRepository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(Board entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived board.");
        }
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Board entity) {
        var oldBoard = boardRepository.findById(entity.getId());
        if (oldBoard.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent workspace!");
        }
        if (!oldBoard.get().getArchived() && !entity.getArchived()) {
            throw new BadRequestException("Archived board cannot be updated.");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldBoard.get(), entity);
        if (!oldBoard.get().getWorkspaceId().equals(entity.getWorkspaceId())) {
            exceptions.append("Board cannot be transferred to another workspace.");
        }
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }
}
