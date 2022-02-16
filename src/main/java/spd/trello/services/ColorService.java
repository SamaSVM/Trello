package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Color;
import spd.trello.repository.ColorRepository;

import java.util.UUID;

@Service
public class ColorService extends AbstractService<Color, ColorRepository> {

    public ColorService(ColorRepository repository) {
        super(repository);
    }

    @Override
    public Color update(Color entity) {
        Color oldCard = getById(entity.getId());
        entity.setLabelId(oldCard.getLabelId());
        if (entity.getRed() == null) {
            entity.setRed(oldCard.getRed());
        }
        if (entity.getGreen() == null) {
            entity.setGreen(oldCard.getGreen());
        }
        if (entity.getBlue() == null) {
            entity.setBlue(oldCard.getBlue());
        }
        return repository.save(entity);
    }

    public void deleteColorForLabel(UUID labelId) {
        Color color = repository.findByLabelId(labelId);
        delete(color.getId());
    }
}
