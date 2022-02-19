package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CheckableItem;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CheckableItemRepository;

import java.util.UUID;

@Service
public class CheckableItemService extends AbstractService<CheckableItem, CheckableItemRepository> {

    public CheckableItemService(CheckableItemRepository repository) {
        super(repository);
    }

    @Override
    public CheckableItem update(CheckableItem entity) {
        CheckableItem oldCheckableItem = getById(entity.getId());
        entity.setChecklistId(oldCheckableItem.getChecklistId());
        if (entity.getName() == null) {
            entity.setName(oldCheckableItem.getName());
        }
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void deleteCheckableItemsForChecklist(UUID checklistId) {
        repository.findAllByChecklistId(checklistId).forEach(checkableItem -> delete(checkableItem.getId()));
    }
}
