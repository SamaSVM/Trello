package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.perent.Resource;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Component
public class HelperValidator<T extends Resource> {

    private final MemberRepository memberRepository;

    public HelperValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public StringBuilder checkCreateEntity(T entity) {
        StringBuilder exceptions = new StringBuilder();
        if (LocalDateTime.now().minusMinutes(1L).isAfter(entity.getCreatedDate()) ||
                LocalDateTime.now().plusMinutes(1L).isBefore(entity.getCreatedDate())) {
            exceptions.append("The createdDate should not be past or future. \n");
        }
        return exceptions;
    }

    public StringBuilder checkUpdateEntity(T oldEntity, T newEntity) {
        StringBuilder exceptions = new StringBuilder();
        if (newEntity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled. \n");
        }
        if (newEntity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled. \n");
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
        return exceptions;
    }

    public void throwException(StringBuilder exceptions) {
        if (exceptions.length() != 0) {
            throw new BadRequestException(exceptions.toString());
        }
    }

    public void validMembersId(StringBuilder exceptions, Set<UUID> membersId) {
        membersId.forEach(id -> {
            if (!memberRepository.existsById(id)) {
                exceptions.append(id).append(" - memberId must belong to the member. \n");
            }
        });
    }
}
