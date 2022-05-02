package spd.trello.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spd.trello.domain.CheckableItem;
import spd.trello.repository.CheckableItemRepository;
import spd.trello.validators.CheckableItemValidator;

import java.util.UUID;

@Slf4j
@Service
public class CheckableItemService extends AbstractService
        <CheckableItem, CheckableItemRepository, CheckableItemValidator> {

    public CheckableItemService(CheckableItemRepository repository, CheckableItemValidator validator) {
        super(repository, validator);
    }

    public void deleteCheckableItemsForChecklist(UUID checklistId) {
        log.debug("Cascade delete checkableitems for checklist with id - {}", checklistId);
        repository.findAllByChecklistId(checklistId).forEach(checkableItem -> delete(checkableItem.getId()));
    }
}
