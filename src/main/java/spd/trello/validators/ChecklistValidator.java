package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Checklist;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CardRepository;
import spd.trello.repository.ChecklistRepository;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Component
public class ChecklistValidator extends AbstractValidator<Checklist> {
    private final CardRepository cardRepository;
    private final ChecklistRepository checklistRepository;
    private final HelperValidator<Checklist> helper;

    public ChecklistValidator(CardRepository cardRepository, ChecklistRepository checklistRepository,
                              HelperValidator<Checklist> helper) {
        this.cardRepository = cardRepository;
        this.checklistRepository = checklistRepository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(Checklist entity) {
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        checkCheckListFields(exceptions, entity);
        if (entity.getCardId() == null || !cardRepository.existsById(entity.getCardId())) {
            exceptions.append("The cardId field must belong to a card. \n");
        }
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Checklist entity) {
        var oldCheckList = checklistRepository.findById(entity.getId());
        if (oldCheckList.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent checklist!");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldCheckList.get(), entity);
        checkCheckListFields(exceptions, entity);
        if (!oldCheckList.get().getCardId().equals(entity.getCardId())) {
            exceptions.append("CheckList cannot be transferred to another card. \n");
        }
        helper.throwException(exceptions);
    }

    private void checkCheckListFields(StringBuilder exceptions, Checklist entity) {
        if (entity.getName() == null) {
            throw new BadRequestException("The name field must be filled.");
        }
        if (entity.getName().length() < 2 || entity.getName().length() > 20) {
            exceptions.append("The name field must be between 2 and 20 characters long. \n");
        }
    }
}
