package spd.trello.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.domain.perent.Resource;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
public class HelperValidator<T extends Resource> {

    private final MemberRepository memberRepository;

    @Autowired
    public HelperValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public StringBuilder checkCreateEntity(T entity) {
        StringBuilder exceptions = new StringBuilder();
        checkResourceFields(exceptions, entity);
        if (LocalDateTime.now().minusMinutes(1L).isAfter(entity.getCreatedDate()) ||
                LocalDateTime.now().plusMinutes(1L).isBefore(entity.getCreatedDate())) {
            exceptions.append("The createdDate should not be past or future. \n");
        }
        return exceptions;
    }

    public StringBuilder checkUpdateEntity(T oldEntity, T newEntity) {
        StringBuilder exceptions = new StringBuilder();
        checkResourceFields(exceptions, newEntity);
        if (newEntity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled.");
        }
        if (newEntity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled.");
        }
        if (LocalDateTime.now().minusMinutes(1L).isAfter(newEntity.getUpdatedDate()) ||
                LocalDateTime.now().plusMinutes(1L).isBefore(newEntity.getUpdatedDate())) {
            exceptions.append("The updatedDate should not be past or future. \n");
        }
        if (!oldEntity.getCreatedBy().equals(newEntity.getCreatedBy())) {
            exceptions.append("The createdBy field cannot be updated. \n");
        }
        if (!oldEntity.getCreatedDate().equals(newEntity.getCreatedDate())) {
            exceptions.append("The createdDate field cannot be updated. \n");
        }
        if (newEntity.getUpdatedBy().length() < 2 || newEntity.getUpdatedBy().length() > 30) {
            exceptions.append("UpdatedBy should be between 2 and 30 characters! \n");
        }
        return exceptions;
    }

    public void throwException(StringBuilder exceptions) {
        if (exceptions.length() != 0) {
            throw new BadRequestException(exceptions.toString());
        }
    }

    private void checkResourceFields(StringBuilder exceptions, T entity) {
        if (entity.getCreatedBy() == null || entity.getCreatedDate() == null) {
            throw new BadRequestException("The createdBy, createdDate fields must be filled.");
        }
        if (entity.getCreatedBy().length() < 2 || entity.getCreatedBy().length() > 30) {
            exceptions.append("CreatedBy should be between 2 and 30 characters! \n");
        }
    }

    public void validMembersId(StringBuilder exceptions, Set<UUID> membersId) {
        if (membersId.isEmpty()) {
            throw new ResourceNotFoundException("The resource must belong to at least one member!");
        }
        membersId.forEach(id -> {
            if (!memberRepository.existsById(id)) {
                exceptions.append(id).append(" - memberId must belong to the member. \n");
            }
        });
    }
}
