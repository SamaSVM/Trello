package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Reminder;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ReminderService extends AbstractService<Reminder> {
    public ReminderService(InterfaceRepository<Reminder> repository, ReminderCardService reminderCardService) {
        super(repository);
        this.reminderCardService = reminderCardService;
    }

    private final ReminderCardService reminderCardService;

    @Override
    public Reminder create(Reminder entity) {
        Reminder reminder = new Reminder();
        reminder.setId(UUID.randomUUID());
        reminder.setCreatedBy(entity.getCreatedBy());
        reminder.setCreatedDate(Date.valueOf(LocalDate.now()));
        reminder.setStart(Date.valueOf(LocalDate.now()));
        reminder.setEnd(entity.getEnd());
        reminder.setRemindOn(entity.getRemindOn());
        reminder.setCardId(entity.getCardId());
        repository.create(reminder);
        return repository.findById(reminder.getId());
    }

    @Override
    public Reminder update(Reminder entity) {
        Reminder oldReminder = repository.findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getEnd() == null) {
            entity.setEnd(oldReminder.getEnd());
        }
        if (entity.getRemindOn() == null) {
            entity.setRemindOn(oldReminder.getRemindOn());
        }
        if (entity.getActive() == null) {
            entity.setActive(oldReminder.getActive());
        }
        return repository.update(entity);
    }

    public List<Reminder> getAllReminders(UUID cardId) {
        return reminderCardService.getAllCommentsForCard(cardId);
    }
}
