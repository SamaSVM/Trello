package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Card;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CardListRepository;
import spd.trello.repository.CardRepository;
import spd.trello.repository.MemberRepository;

import java.util.Set;
import java.util.UUID;

@Component
public class CardValidator extends AbstractValidator<Card> {
    private final CardListRepository cardListRepository;
    private final MemberRepository memberRepository;
    private final ReminderValidator reminderValidator;
    private final CardRepository cardRepository;

    public CardValidator(CardListRepository cardListRepository, MemberRepository memberRepository,
                         ReminderValidator reminderValidator, CardRepository cardRepository) {
        this.cardListRepository = cardListRepository;
        this.memberRepository = memberRepository;
        this.reminderValidator = reminderValidator;
        this.cardRepository = cardRepository;
    }

    @Override
    public void validateSaveEntity(Card entity) {

        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived card.");
        }
        reminderValidator.validateSaveEntity(entity.getReminder());

        if (!cardListRepository.existsById(entity.getCardListId())) {
            throw new BadRequestException("The cardListId field must belong to a CardList.");
        }
        validMembersId(entity.getMembersId());
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Card entity) {
        Card oldCard = cardRepository.getById(entity.getId());
        if (!oldCard.getArchived() && !entity.getArchived()) {
            throw new BadRequestException("Archived CardList cannot be updated.");
        }
        if (!oldCard.getCreatedBy().equals(entity.getCreatedBy())) {
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if (!oldCard.getCreatedDate().equals(entity.getCreatedDate())) {
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if (!oldCard.getCardListId().equals(entity.getCardListId())) {
            throw new BadRequestException("Card cannot be transferred to another CardList.");
        }
        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled.");
        }
        if (entity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled.");
        }
        validMembersId(entity.getMembersId());
        reminderValidator.validateUpdateEntity(entity.getReminder());
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
