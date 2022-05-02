package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Label;
import spd.trello.repository.LabelRepository;
import spd.trello.validators.LabelValidator;

import java.util.UUID;

@Service
public class LabelService extends AbstractService<Label, LabelRepository, LabelValidator> {
    public LabelService(LabelRepository repository, LabelValidator validator) {
        super(repository, validator);
    }

    public void deleteLabelsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(label -> delete(label.getId()));
    }
}
