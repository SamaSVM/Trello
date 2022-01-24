package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Checklist;
import spd.trello.repository.ChecklistCardRepository;


import java.util.List;
import java.util.UUID;

@Service
public class ChecklistCardService {
    public ChecklistCardService(ChecklistCardRepository repository) {
        this.repository = repository;
    }

    private final ChecklistCardRepository repository;


    public List<Checklist> getAllChecklistsForCard(UUID cardId) {
        return repository.findAllChecklistForCard(cardId);
    }
}
