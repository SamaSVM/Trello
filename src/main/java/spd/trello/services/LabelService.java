package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Label;
import spd.trello.repository.InterfaceRepository;

import java.util.List;
import java.util.UUID;

@Service
public class LabelService extends AbstractService<Label>{
    public LabelService(InterfaceRepository<Label> repository, LabelCardService labelCardService) {
        super(repository);
        this.labelCardService = labelCardService;
    }

    private final LabelCardService labelCardService;

    @Override
    public Label create(Label entity) {
        Label label = new Label();
        label.setId(UUID.randomUUID());
        label.setName(entity.getName());
        label.setColorId(entity.getColorId());
        label.setCardId(entity.getCardId());
        repository.create(label);
        return repository.findById(label.getId());
    }

    @Override
    public Label update(Label entity) {
        Label oldLabel = repository.findById(entity.getId());
        if (entity.getName() == null) {
            entity.setName(oldLabel.getName());
        }
        if (entity.getColorId() == null) {
            entity.setColorId(oldLabel.getColorId());
        }
        return repository.update(entity);
    }

        public List<Label> getAllLabels(UUID cardId) {
        return labelCardService.getAllLabelsForCard(cardId);
    }
}
