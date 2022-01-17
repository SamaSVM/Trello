package spd.trello.services;

import spd.trello.domain.Checklist;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class ChecklistService extends AbstractService<Checklist>{
    public ChecklistService(InterfaceRepository<Checklist> repository) {
        super(repository);
    }

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

    private void checkMember(Member member){
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This member cannot update checklist!");
        }
    }
}
