package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Workspace;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.WorkspaceRepository;

import java.util.Set;
import java.util.UUID;

@Component
public class WorkspaceValidator extends AbstractValidator<Workspace> {

    private final WorkspaceRepository workspaceRepository;
    private final HelperValidator<Workspace> helper;

    public WorkspaceValidator( WorkspaceRepository workspaceRepository, HelperValidator<Workspace> helper) {
        this.workspaceRepository = workspaceRepository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(Workspace entity) {
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Workspace entity) {
        var oldWorkspace = workspaceRepository.findById(entity.getId());
        if (oldWorkspace.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent workspace!");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldWorkspace.get(), entity);
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }


}
