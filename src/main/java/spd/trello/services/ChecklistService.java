package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Checklist;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.ChecklistRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ChecklistService extends AbstractService<Checklist, ChecklistRepository> {
    public ChecklistService(ChecklistRepository repository, CheckableItemService checkableItemService) {
        super(repository);
        this.checkableItemService = checkableItemService;
    }

    private final CheckableItemService checkableItemService;

    @Override
    public Checklist save(Checklist entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Checklist update(Checklist entity) {
        Checklist oldChecklist = getById(entity.getId());

        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("Not found updated by!");
        }

        if (entity.getName() == null) {
            throw new ResourceNotFoundException();
        }

        entity.setCreatedBy(oldChecklist.getCreatedBy());
        entity.setCreatedDate(oldChecklist.getCreatedDate());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCardId(oldChecklist.getCardId());

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        checkableItemService.deleteCheckableItemsForChecklist(id);
        super.delete(id);
    }

    public void deleteCheckListsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(checklist -> delete(checklist.getId()));
    }
}
