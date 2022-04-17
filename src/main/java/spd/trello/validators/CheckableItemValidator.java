package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.CheckableItem;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CheckableItemRepository;
import spd.trello.repository.ChecklistRepository;

@Component
public class CheckableItemValidator extends AbstractValidator<CheckableItem> {
    private final ChecklistRepository checklistRepository;
    private final CheckableItemRepository checkableItemRepository;

    public CheckableItemValidator(ChecklistRepository checklistRepository, CheckableItemRepository checkableItemRepository) {
        this.checklistRepository = checklistRepository;
        this.checkableItemRepository = checkableItemRepository;
    }

    @Override
    public void validateSaveEntity(CheckableItem entity) {
        if (!checklistRepository.existsById(entity.getChecklistId())) {
            throw new BadRequestException("The checklistId field must belong to a checklist.");
        }
    }

    @Override
    public void validateUpdateEntity(CheckableItem entity) {
        CheckableItem oldCheckableItem = checkableItemRepository.getById(entity.getId());
        if (!oldCheckableItem.getChecklistId().equals(entity.getChecklistId())) {
            throw new BadRequestException("CheckableItem cannot be transferred to another checklist.");
        }
    }
}
