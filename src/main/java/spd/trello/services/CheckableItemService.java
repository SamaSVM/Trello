package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CheckableItem;
import spd.trello.repository.InterfaceRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CheckableItemService extends AbstractService<CheckableItem>{
    public CheckableItemService(InterfaceRepository<CheckableItem> repository, CheckableItemChecklistService checkableItemChecklistService) {
        super(repository);
        this.checkableItemChecklistService = checkableItemChecklistService;
    }

    private final CheckableItemChecklistService checkableItemChecklistService;

    @Override
    public CheckableItem create(CheckableItem entity) {
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setId(UUID.randomUUID());
        checkableItem.setName(entity.getName());
        checkableItem.setChecklistId(entity.getChecklistId());
        repository.create(checkableItem);
        return repository.findById(checkableItem.getId());
    }

    @Override
    public CheckableItem update(CheckableItem entity) {
        CheckableItem oldCheckableItem = repository.findById(entity.getId());
        if (entity.getName() == null) {
            entity.setName(oldCheckableItem.getName());
        }
        if (entity.getChecked() == null) {
            entity.setChecked(oldCheckableItem.getChecked());
        }
        return repository.update(entity);
    }

    public List<CheckableItem> getCheckableItems(UUID checklistId) {
        return checkableItemChecklistService.getAllCheckableItemForChecklist(checklistId);
    }
}
