package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Member;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.MemberRepository;
import spd.trello.repository.UserRepository;

@Component
public class MemberValidator extends AbstractValidator<Member> {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public MemberValidator(UserRepository userRepository, MemberRepository memberRepository) {
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void validateSaveEntity(Member entity) {
        if (!userRepository.existsById(entity.getUserId())) {
            throw new BadRequestException("The userId field must belong to a user.");
        }
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Member entity) {
        Member oldMember = memberRepository.getById(entity.getId());
        if (!oldMember.getCreatedBy().equals(entity.getCreatedBy())) {
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if (!oldMember.getCreatedDate().equals(entity.getCreatedDate())) {
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if (!oldMember.getUserId().equals(entity.getUserId())) {
            throw new BadRequestException("Member cannot be transferred to another user.");
        }
        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled.");
        }
        if (entity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled.");
        }
        super.validateUpdateEntity(entity);
    }
}
