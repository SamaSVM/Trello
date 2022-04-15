package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Board;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.MemberRepository;

import java.util.Set;
import java.util.UUID;

@Component
public class BoardValidator extends AbstractValidator<Board>{

    private final MemberRepository memberRepository;

    private final BoardRepository boardRepository;

    public BoardValidator(MemberRepository memberRepository, BoardRepository boardRepository) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
    }

    @Override
    public void validateSaveEntity(Board entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived board.");
        }
        validMembersId(entity.getMembersId());
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Board entity) {
        Board oldBoard = boardRepository.getById(entity.getId());
        if(!oldBoard.getArchived() && !entity.getArchived()){
            throw new BadRequestException("Archived board cannot be updated.");
        }
        if (!oldBoard.getWorkspaceId().equals(entity.getWorkspaceId())) {
            throw new BadRequestException("Board cannot be transferred to another workspace.");
        }
        if(!oldBoard.getCreatedBy().equals(entity.getCreatedBy())){
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if(!oldBoard.getCreatedDate().equals(entity.getCreatedDate())){
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
        for (UUID id: membersId) {
            if (memberRepository.existsById(id)) {
                throw new BadRequestException(id + " - memberId must belong to the member.");
            }
        }
    }
}
