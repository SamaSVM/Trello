package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Reminder;

import java.util.List;
import java.util.UUID;

@Repository
public class ReminderRepository implements InterfaceRepository<Reminder> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT = "INSERT INTO reminders " +
            "(id, created_by, created_date, start, \"end\", remind_on, active, card_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM reminders WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM reminders;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM reminders WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE reminders SET " +
            "updated_by=?, updated_date=?, \"end\"=?, remind_on=?, active=? WHERE id=?;";

    @Override
    public Reminder findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Reminder.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Reminder with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Reminder> findAll() {
        List<Reminder> result = jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Reminder.class));
        if (result.isEmpty()) {
            throw new IllegalStateException("Table reminders is empty!");
        }
        return result;
    }

    @Override
    public void create(Reminder entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getStart(),
                    entity.getEnd(),
                    entity.getRemindOn(),
                    entity.getActive(),
                    entity.getCardId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Reminder doesn't creates");
        }
    }

    @Override
    public Reminder update(Reminder entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getEnd(),
                    entity.getRemindOn(),
                    entity.getActive(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Reminder with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("ReminderRepository::delete failed", e);
        }
    }
}
