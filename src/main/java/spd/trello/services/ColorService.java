package spd.trello.services;

import spd.trello.domain.Color;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.util.UUID;

public class ColorService extends AbstractService<Color> {
    public ColorService(InterfaceRepository<Color> repository) {
        super(repository);
    }

    public Color create(Member member, UUID labelId, Integer red, Integer green, Integer blue) {
        checkMember(member);
        Color color = new Color();
        color.setId(UUID.randomUUID());
        color.setRed(red);
        color.setGreen(green);
        color.setBlue(blue);
        color.setLabelId(labelId);
        repository.create(color);
        return repository.findById(color.getId());
    }

    public Color update(Member member, Color entity) {
        checkMember(member);
        Color oldCard = repository.findById(entity.getId());
        if (entity.getRed() == null) {
            entity.setRed(oldCard.getRed());
        }
        if (entity.getGreen() == null) {
            entity.setGreen(oldCard.getGreen());
        }
        if (entity.getBlue() == null) {
            entity.setBlue(oldCard.getBlue());
        }
        return repository.update(entity);
    }

    private void checkMember(Member member) {
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This member cannot update cardList!");
        }
    }
}
