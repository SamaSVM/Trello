package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Reminder;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends AbstractRepository<Reminder> {
    List<Reminder> getAllByRemindOnAfterAndActive(LocalDateTime remindOn, Boolean active);
}

