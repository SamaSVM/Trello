package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Card;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CardListRepository;
import spd.trello.repository.CardRepository;

@Component
public class CardValidator extends AbstractValidator<Card> {
    private final CardListRepository cardListRepository;
    private final ReminderValidator reminderValidator;
    private final CardRepository cardRepository;
    private final HelperValidator<Card> helper;

    public CardValidator(CardListRepository cardListRepository, ReminderValidator reminderValidator,
                         CardRepository cardRepository, HelperValidator<Card> helper) {
        this.cardListRepository = cardListRepository;
        this.reminderValidator = reminderValidator;
        this.cardRepository = cardRepository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(Card entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived card.");
        }
        reminderValidator.validateSaveEntity(entity.getReminder());
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        if (!cardListRepository.existsById(entity.getCardListId())) {
            exceptions.append("The cardListId field must belong to a CardList.");
        }
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Card entity) {
        var oldCard = cardRepository.findById(entity.getId());
        if (oldCard.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent card!");
        }
        if (!oldCard.get().getArchived() && !entity.getArchived()) {
            throw new BadRequestException("Archived CardList cannot be updated.");
        }
        reminderValidator.validateUpdateEntity(entity.getReminder());
        StringBuilder exceptions = helper.checkUpdateEntity(oldCard.get(), entity);
        if (!oldCard.get().getCardListId().equals(entity.getCardListId())) {
            exceptions.append("Card cannot be transferred to another CardList.");
        }
        helper.validMembersId(exceptions, entity.getMembersId());
        helper.throwException(exceptions);
    }
}
