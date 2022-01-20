package spd.trello.repository;

import spd.trello.domain.Comment;
import spd.trello.domain.Reminder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReminderCardRepository {
    public  ReminderCardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM reminders WHERE card_id=?;";

    public List<Reminder> findAllRemindersForCard(UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CARD_ID_STMT)) {
            List<Reminder> result = new ArrayList<>();
            statement.setObject(1, cardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(" ReminderCardRepository::findAllReminderForCard failed", e);
        }
        throw new IllegalStateException(" Reminders for card with ID: " + cardId.toString() + " doesn't exists");
    }

    private Reminder map(ResultSet rs) throws SQLException {
        Reminder reminder = new Reminder();
        reminder.setId(UUID.fromString(rs.getString("id")));
        reminder.setCreatedBy(rs.getString("created_by"));
        reminder.setUpdatedBy(rs.getString("updated_by"));
        reminder.setCreatedDate(rs.getDate("created_date"));
        reminder.setUpdatedDate(rs.getDate("updated_date"));
        reminder.setStart(rs.getDate("start"));
        reminder.setEnd(rs.getDate("end"));
        reminder.setRemindOn(rs.getDate("remind_on"));
        reminder.setActive(rs.getBoolean("active"));
        reminder.setCardId(UUID.fromString(rs.getString("card_id")));
        return reminder;
    }
}
