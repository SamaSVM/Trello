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

    private final MemberRepository memberRepository;

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceValidator(MemberRepository memberRepository, WorkspaceRepository workspaceRepository) {
        this.memberRepository = memberRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public void validateSaveEntity(Workspace entity) {
        validMembersId(entity.getMembersId());
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Workspace entity) {
        Workspace oldWorkspace = workspaceRepository.getById(entity.getId());
        if (!oldWorkspace.getCreatedBy().equals(entity.getCreatedBy())) {
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if (!oldWorkspace.getCreatedDate().equals(entity.getCreatedDate())) {
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled.");
        }
        if (entity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled.");
        }
        validMembersId(entity.getMembersId());
        super.validateUpdateEntity(entity);
    }

    private void validMembersId(Set<UUID> membersId) {
        for (UUID id : membersId) {
            if (memberRepository.existsById(id)) {
                throw new BadRequestException(id + " - memberId must belong to the member.");
            }
        }
    }
}
