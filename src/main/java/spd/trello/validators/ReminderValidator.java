package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Reminder;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.ReminderRepository;

@Component
public class ReminderValidator extends AbstractValidator<Reminder> {

    private final ReminderRepository repository;

    public ReminderValidator(ReminderRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validateSaveEntity(Reminder entity) {
        checkTime(entity);
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Reminder entity) {
        Reminder oldReminder = repository.getById(entity.getId());
        if (!oldReminder.getActive() && !entity.getActive()) {
            throw new BadRequestException("Cannot update an inactive remainder.");
        }
        if (!oldReminder.getCreatedBy().equals(entity.getCreatedBy())) {
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if (!oldReminder.getCreatedDate().equals(entity.getCreatedDate())) {
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled.");
        }
        if (entity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled.");
        }
        checkTime(entity);
        super.validateUpdateEntity(entity);
    }

    public void checkTime(Reminder entity) {
        if (entity.getStart().isBefore(entity.getEnd())) {
            throw new BadRequestException("Start cannot be later than end.");
        }
        if (entity.getStart().isAfter(entity.getRemindOn()) || entity.getEnd().isBefore(entity.getRemindOn())) {
            throw new BadRequestException("The remindOn should be between start and end.");
        }
    }
}
