package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Reminder;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.ReminderRepository;

@Component
public class ReminderValidator extends AbstractValidator<Reminder> {

    private final ReminderRepository repository;
    private final HelperValidator<Reminder> helper;

    public ReminderValidator(ReminderRepository repository, HelperValidator<Reminder> helper) {
        this.repository = repository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(Reminder entity) {
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        checkTime(exceptions, entity);
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Reminder entity) {
        var oldReminder = repository.findById(entity.getId());
        if (oldReminder.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent reminder!");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldReminder.get(), entity);
        if (!oldReminder.get().getActive() && !entity.getActive()) {
            exceptions.append("Cannot update an inactive remainder. \n");
        }
        checkTime(exceptions, entity);
        helper.throwException(exceptions);
    }

    public void checkTime(StringBuilder exceptions, Reminder entity) {
        if (entity.getStart().isAfter(entity.getEnd())) {
            exceptions.append("Start cannot be later than end. \n");
        }
        if (entity.getStart().isAfter(entity.getRemindOn()) || entity.getEnd().isBefore(entity.getRemindOn())) {
            exceptions.append("The remindOn should be between start and end. \n");
        }
    }
}
