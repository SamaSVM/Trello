package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Reminder;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.ReminderRepository;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class ReminderService extends AbstractService<Reminder, ReminderRepository> {

    public ReminderService(ReminderRepository repository) {
        super(repository);
    }

    @Override
    public Reminder save(Reminder entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Reminder update(Reminder entity) {
        Reminder oldReminder = getById(entity.getId());

        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("Not found updated by!");
        }

        if (entity.getRemindOn() == null && entity.getStart() == null && entity.getEnd() == null
                && entity.getActive() == oldReminder.getActive()) {
            throw new ResourceNotFoundException();
        }

        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldReminder.getCreatedBy());
        entity.setCreatedDate(oldReminder.getCreatedDate());
        if (entity.getStart() == null) {
            entity.setStart(oldReminder.getStart());
        }
        if (entity.getEnd() == null) {
            entity.setEnd(oldReminder.getEnd());
        }
        if (entity.getRemindOn() == null) {
            entity.setRemindOn(oldReminder.getRemindOn());
        }
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
