package spd.trello.repository;

import org.springframework.stereotype.Repository;
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
public class ReminderRepository implements InterfaceRepository<Reminder> {
    public ReminderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT = "INSERT INTO reminders " +
            "(id, created_by, created_date, start, \"end\", remind_on, active, card_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM reminders WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM reminders;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM reminders WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE reminders SET " +
            "updated_by=?, updated_date=?, \"end\"=?, remind_on=?, active=? WHERE id=?;";

    @Override
    public Reminder findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("ReminderRepository::findById failed", e);
        }
        throw new IllegalStateException("Reminder with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Reminder> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Reminder> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("ReminderRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table reminders is empty!");
    }

    @Override
    public void create(Reminder entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setDate(4, entity.getStart());
            statement.setDate(5, entity.getEnd());
            statement.setDate(6, entity.getRemindOn());
            statement.setBoolean(7, entity.getActive());
            statement.setObject(8, entity.getCardId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Reminder doesn't creates");
        }
    }

    @Override
    public Reminder update(Reminder entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            statement.setDate(3, entity.getEnd());
            statement.setDate(4, entity.getRemindOn());
            statement.setBoolean(5, entity.getActive());
            statement.setObject(6, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Reminder with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_STMT)) {
            statement.setObject(1, id);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("ReminderRepository::delete failed", e);
        }
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
