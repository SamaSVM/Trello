package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Label;
import spd.trello.repository.LabelRepository;

import java.util.List;
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
        entity.setCardId(oldLabel.getCardId());
        if (entity.getName() == null) {
            entity.setName(oldLabel.getName());
        }
        return repository.save(entity);
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
