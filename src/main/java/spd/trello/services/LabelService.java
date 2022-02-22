package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Label;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.LabelRepository;

import java.util.UUID;

@Service
public class LabelService extends AbstractService<Label, LabelRepository> {
    public LabelService(LabelRepository repository, ColorService colorService) {
        super(repository);
        this.colorService = colorService;
    }

    private final ColorService colorService;

    @Override
    public Label update(Label entity) {
        Label oldLabel = getById(entity.getId());

        if (entity.getName() == null) {
            throw new ResourceNotFoundException();
        }

        entity.setCardId(oldLabel.getCardId());

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        colorService.deleteColorForLabel(id);
        super.delete(id);
    }

    public void deleteLabelsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(label -> delete(label.getId()));
    }
}
