package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Color;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.ColorRepository;

import java.util.UUID;

@Service
public class ColorService extends AbstractService<Color, ColorRepository> {

    public ColorService(ColorRepository repository) {
        super(repository);
    }

    @Override
    public Color save(Color entity) {
        throw new BadRequestException("This save method cannot be called!");
    }

    @Override
    public Color update(Color entity) {
        Color oldCard = getById(entity.getId());

        if (entity.getRed().equals(oldCard.getRed())
                && entity.getGreen().equals(oldCard.getGreen())
                && entity.getBlue().equals(oldCard.getBlue())) {
            throw new ResourceNotFoundException();
        }

        if (entity.getRed() == null) {
            entity.setRed(oldCard.getRed());
        }
        if (entity.getGreen() == null) {
            entity.setGreen(oldCard.getGreen());
        }
        if (entity.getBlue() == null) {
            entity.setBlue(oldCard.getBlue());
        }
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        throw new BadRequestException("This delete method cannot be called!");
    }
}
