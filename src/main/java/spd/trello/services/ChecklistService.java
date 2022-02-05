package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Checklist;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ChecklistService extends AbstractService<Checklist> {
    public ChecklistService(InterfaceRepository<Checklist> repository, ChecklistCardService checklistCardService) {
        super(repository);
        this.checklistCardService = checklistCardService;
    }

    private final ChecklistCardService checklistCardService;

    @Override
    public Checklist create(Checklist entity) {
        Checklist checklist = new Checklist();
        checklist.setId(UUID.randomUUID());
        checklist.setCreatedBy(entity.getCreatedBy());
        checklist.setCreatedDate(Date.valueOf(LocalDate.now()));
        checklist.setName(entity.getName());
        checklist.setCardId(entity.getCardId());
        repository.create(checklist);
        return repository.findById(checklist.getId());
    }

    @Override
    public Checklist update(Checklist entity) {
        Checklist oldChecklist = repository.findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldChecklist.getName());
        }
        return repository.update(entity);
    }

        public List<Checklist> getAllChecklists(UUID cardId) {
        return checklistCardService.getAllChecklistsForCard(cardId);
    }
}
