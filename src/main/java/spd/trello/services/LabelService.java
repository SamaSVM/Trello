package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Label;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.util.UUID;

@Service
public class LabelService extends AbstractService<Label>{
    public LabelService(InterfaceRepository<Label> repository) {
        super(repository);
    }

    public Label create(Member member, UUID cardId, String name) {
        checkMember(member);
        Label label = new Label();
        label.setId(UUID.randomUUID());
        label.setName(name);
        label.setCardId(cardId);
        repository.create(label);
        return repository.findById(label.getId());
    }

    public Label update(Member member, Label entity) {
        Label oldLabel = repository.findById(entity.getId());
        checkMember(member);
        if (entity.getName() == null) {
            entity.setName(oldLabel.getName());
        }
        return repository.update(entity);
    }

    private  void checkMember(Member member){
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This member cannot update label!");
        }
    }
}
