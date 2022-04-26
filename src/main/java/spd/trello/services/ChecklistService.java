package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Checklist;
import spd.trello.repository.ChecklistRepository;
import spd.trello.validators.ChecklistValidator;

import java.util.UUID;

@Service
public class ChecklistService extends AbstractService<Checklist, ChecklistRepository, ChecklistValidator> {
    public ChecklistService
            (ChecklistRepository repository, CheckableItemService checkableItemService, ChecklistValidator validator) {
        super(repository, validator);
        this.checkableItemService = checkableItemService;
    }

    private final CheckableItemService checkableItemService;

    @Override
    public void delete(UUID id) {
        checkableItemService.deleteCheckableItemsForChecklist(id);
        super.delete(id);
    }

    public void deleteCheckListsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(checklist -> delete(checklist.getId()));
    }
}
