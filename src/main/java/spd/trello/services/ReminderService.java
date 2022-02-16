package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Reminder;
import spd.trello.repository.ReminderRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class ReminderService extends AbstractService<Reminder, ReminderRepository> {

    public ReminderService(ReminderRepository repository) {
        super(repository);
    }

    @Override
    public Reminder save(Reminder entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        return repository.save(entity);
    }

    @Override
    public Reminder update(Reminder entity) {
        Reminder oldReminder = getById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldReminder.getCreatedBy());
        entity.setCreatedDate(oldReminder.getCreatedDate());
        entity.setCardId(oldReminder.getCardId());
        if (entity.getStart() == null) {
            entity.setStart(oldReminder.getStart());
        }
        if (entity.getEnd() == null) {
            entity.setEnd(oldReminder.getEnd());
        }
        if (entity.getRemindOn() == null) {
            entity.setRemindOn(oldReminder.getRemindOn());
        }
        return repository.save(entity);
    }

    public void deleteReminderForCard(UUID cardId) {
        Reminder reminder = repository.getByCardId(cardId);
        if(reminder != null) {
            delete(reminder.getId());
        }
    }
}
