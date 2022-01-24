package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CheckableItem;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.util.UUID;

@Service
public class CheckableItemService extends AbstractService<CheckableItem>{
    public CheckableItemService(InterfaceRepository<CheckableItem> repository) {
        super(repository);
    }

    public CheckableItem create(Member member, UUID checklistId, String name) {
        checkMember(member);
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setId(UUID.randomUUID());
        checkableItem.setName(name);
        checkableItem.setChecklistId(checklistId);
        repository.create(checkableItem);
        return repository.findById(checkableItem.getId());
    }

    public CheckableItem update(Member member, CheckableItem entity) {
        checkMember(member);
        CheckableItem oldCheckableItem = repository.findById(entity.getId());
        if (entity.getName() == null) {
            entity.setName(oldCheckableItem.getName());
        }
        if (entity.getChecked() == null) {
            entity.setChecked(oldCheckableItem.getChecked());
        }
        return repository.update(entity);
    }

    private void checkMember(Member member){
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This member cannot update checkableItem!");
        }
    }
}
