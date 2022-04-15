package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CheckableItem;
import spd.trello.repository.CheckableItemRepository;
import spd.trello.validators.CheckableItemValidator;

import java.util.UUID;

@Service
public class CheckableItemService extends AbstractService
        <CheckableItem, CheckableItemRepository, CheckableItemValidator> {

    public CheckableItemService(CheckableItemRepository repository, CheckableItemValidator validator) {
        super(repository, validator);
    }

    public void deleteCheckableItemsForChecklist(UUID checklistId) {
        repository.findAllByChecklistId(checklistId).forEach(checkableItem -> delete(checkableItem.getId()));
    }
}
