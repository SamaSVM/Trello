package spd.trello.services;

import spd.trello.domain.Reminder;
import spd.trello.repository.ReminderCardRepository;

import java.util.List;
import java.util.UUID;

public class ReminderCardService {
    public  ReminderCardService( ReminderCardRepository repository) {
        this.repository = repository;
    }

    private final ReminderCardRepository repository;

    public List<Reminder> getAllCommentsForCard(UUID cardId) {
        return repository.findAllRemindersForCard(cardId);
    }
}
