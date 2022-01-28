package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Label;
import spd.trello.domain.Reminder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ReminderCardRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM reminders WHERE card_id=?;";

    public List<Reminder> findAllRemindersForCard(UUID cardId) {
        List<Reminder> result = jdbcTemplate.query(
                FIND_BY_CARD_ID_STMT,
                new Object[]{cardId},
                new BeanPropertyRowMapper<>(Reminder.class));
        if(result.isEmpty()){
            throw new IllegalStateException(" Reminders for card with ID: " + cardId.toString() + " doesn't exists");
        }
        return result;
    }
}
