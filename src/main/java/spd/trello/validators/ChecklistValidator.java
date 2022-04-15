package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Checklist;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CardRepository;
import spd.trello.repository.ChecklistRepository;

@Component
public class ChecklistValidator extends AbstractValidator<Checklist> {
    private final CardRepository cardRepository;
    private final ChecklistRepository checklistRepository;

    public ChecklistValidator(CardRepository cardRepository, ChecklistRepository checklistRepository) {
        this.cardRepository = cardRepository;
        this.checklistRepository = checklistRepository;
    }

    @Override
    public void validateSaveEntity(Checklist entity) {
        if (!cardRepository.existsById(entity.getCardId())) {
            throw new BadRequestException("The cardId field must belong to a card.");
        }
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Checklist entity) {
        Checklist oldCheckList = checklistRepository.getById(entity.getId());
        if (!oldCheckList.getCreatedBy().equals(entity.getCreatedBy())) {
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if (!oldCheckList.getCreatedDate().equals(entity.getCreatedDate())) {
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if (!oldCheckList.getCardId().equals(entity.getCardId())) {
            throw new BadRequestException("CheckList cannot be transferred to another card.");
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
