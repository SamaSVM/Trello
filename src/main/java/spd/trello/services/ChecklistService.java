package spd.trello.services;

import spd.trello.db.ConnectionPool;
import spd.trello.domain.CheckableItem;
import spd.trello.domain.Checklist;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.CheckableItemChecklistRepository;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ChecklistService extends AbstractService<Checklist>{
    public ChecklistService(InterfaceRepository<Checklist> repository) {
        super(repository);
    }

    private final CheckableItemChecklistService checkableItemChecklistService =
            new CheckableItemChecklistService(new CheckableItemChecklistRepository(ConnectionPool.createDataSource()));

    public Checklist create(Member member, UUID cardId, String name) {
        Checklist checklist = new Checklist();
        checklist.setId(UUID.randomUUID());
        checklist.setCreatedBy(member.getCreatedBy());
        checklist.setCreatedDate(Date.valueOf(LocalDate.now()));
        checklist.setName(name);
        checklist.setCardId(cardId);
        repository.create(checklist);
        return repository.findById(checklist.getId());
    }

    public Checklist update(Member member, Checklist entity) {
        checkMember(member);
        Checklist oldChecklist = repository.findById(entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldChecklist.getName());
        }
        return repository.update(entity);
    }

    public List<CheckableItem> getCheckableItems(Member member, UUID checklistId) {
        checkMember(member);
        return checkableItemChecklistService.getAllCheckableItemForChecklist(checklistId);
    }

    private void checkMember(Member member){
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This member cannot update checklist!");
        }
    }
}
