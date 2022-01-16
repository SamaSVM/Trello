package spd.trello.services;


import spd.trello.domain.Member;
import spd.trello.domain.Reminder;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class ReminderService extends AbstractService<Reminder>{
    public ReminderService(InterfaceRepository<Reminder> repository) {
        super(repository);
    }

    public Reminder create(Member member, UUID cardId, Date end, Date remindOn) {
        Reminder reminder = new Reminder();
        reminder.setId(UUID.randomUUID());
        reminder.setCreatedBy(member.getCreatedBy());
        reminder.setCreatedDate(Date.valueOf(LocalDate.now()));
        reminder.setStart(Date.valueOf(LocalDate.now()));
        reminder.setEnd(end);
        reminder.setRemindOn(remindOn);
        reminder.setCardId(cardId);
        repository.create(reminder);
        return repository.findById(reminder.getId());
    }

    public Reminder update(Member member, Reminder entity) {
        checkMember(member);
        Reminder oldReminder = repository.findById(entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
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

    private void checkMember(Member member){
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This member cannot update reminder!");
        }
    }
}
