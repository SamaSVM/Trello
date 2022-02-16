package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Reminder;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReminderRepository extends AbstractRepository<Reminder> {
    Reminder getByCardId(UUID cardId);
}