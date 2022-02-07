package spd.trello.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Reminder;

import java.util.List;
import java.util.UUID;

@Repository
public class ReminderCardRepository {
    public ReminderCardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM reminders WHERE card_id=?;";

    public List<Reminder> findAllRemindersForCard(UUID cardId) {
        return jdbcTemplate.query(
                FIND_BY_CARD_ID_STMT,
                new Object[]{cardId},
                new BeanPropertyRowMapper<>(Reminder.class));
    }
}
