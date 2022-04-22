package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Board;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.WorkspaceRepository;

@Component
public class BoardValidator extends AbstractValidator<Board> {

    private final BoardRepository boardRepository;
    private final WorkspaceRepository workspaceRepository;
    private final HelperValidator<Board> helper;

    public BoardValidator(BoardRepository boardRepository, WorkspaceRepository workspaceRepository,
                          HelperValidator<Board> helper) {
        this.boardRepository = boardRepository;
        this.workspaceRepository = workspaceRepository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(Board entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived board.");
        }
        if (!workspaceRepository.existsById(entity.getWorkspaceId())) {
            throw new ResourceNotFoundException("WorkspaceId must be owned by Workspace.");
        }
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        checkBoardFields(exceptions, entity);
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Board entity) {
        var oldBoard = boardRepository.findById(entity.getId());
        if (oldBoard.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent board!");
        }
        if (oldBoard.get().getArchived() && entity.getArchived()) {
            throw new BadRequestException("Archived board cannot be updated.");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldBoard.get(), entity);
        checkBoardFields(exceptions, entity);
        if (!oldBoard.get().getWorkspaceId().equals(entity.getWorkspaceId())) {
            exceptions.append("Board cannot be transferred to another workspace. \n");
        }
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }

    private void checkBoardFields(StringBuilder exceptions, Board entity) {
        if (entity.getName() == null) {
            throw new BadRequestException("The name field must be filled.");
        }
        if (entity.getName().length() < 2 || entity.getName().length() > 20) {
            exceptions.append("The name field must be between 2 and 20 characters long. \n");
        }
        if (entity.getDescription() != null &&
                (entity.getDescription().length() < 2 || entity.getDescription().length() > 255)) {
            exceptions.append("The description field must be between 2 and 255 characters long. \n");
        }
    }
}
