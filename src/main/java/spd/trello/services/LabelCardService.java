package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Label;
import spd.trello.repository.LabelCardRepository;

import java.util.List;
import java.util.UUID;

@Service
public class LabelCardService {
    public LabelCardService(LabelCardRepository repository) {
        this.repository = repository;
    }

    private final LabelCardRepository repository;

    public List<Label> getAllLabelsForCard(UUID cardId) {
        return repository.findAllLabelsForCard(cardId);
    }
}
