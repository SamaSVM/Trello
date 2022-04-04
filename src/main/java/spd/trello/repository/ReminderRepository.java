package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Reminder;

import java.util.TreeSet;

@Repository
public interface ReminderRepository extends AbstractRepository<Reminder> {
    TreeSet<Reminder> findAllByActive(Boolean active);
}
