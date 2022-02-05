package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Color;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.util.UUID;

@Service
public class ColorService extends AbstractService<Color> {
    public ColorService(InterfaceRepository<Color> repository) {
        super(repository);
    }

    @Override
    public Color create(Color entity) {
        Color color = new Color();
        color.setId(UUID.randomUUID());
        color.setRed(entity.getRed());
        color.setGreen(entity.getGreen());
        color.setBlue(entity.getBlue());
        color.setLabelId(entity.getLabelId());
        repository.create(color);
        return repository.findById(color.getId());
    }

    @Override
    public Color update(Color entity) {
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
}
