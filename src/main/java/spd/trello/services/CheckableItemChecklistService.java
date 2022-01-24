package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CheckableItem;
import spd.trello.repository.CheckableItemChecklistRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CheckableItemChecklistService {
    public CheckableItemChecklistService(CheckableItemChecklistRepository repository) {
        this.repository = repository;
    }

    private final CheckableItemChecklistRepository repository;


    public List<CheckableItem> getAllCheckableItemForChecklist(UUID checklistId) {
        return repository.findAllCheckableItemForChecklist(checklistId);
    }
}
