package spd.trello.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spd.trello.domain.Label;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.LabelRepository;
import spd.trello.validators.LabelValidator;

import java.util.UUID;

@Slf4j
@Service
public class LabelService extends AbstractService<Label, LabelRepository, LabelValidator> {
    public LabelService(LabelRepository repository, LabelValidator validator) {
        super(repository, validator);
    }

    public void deleteLabelsForCard(UUID cardId) {
        log.debug("Cascade delete labels for card with id - {}", cardId);
        repository.findAllByCardId(cardId).forEach(label -> delete(label.getId()));
    }
}
