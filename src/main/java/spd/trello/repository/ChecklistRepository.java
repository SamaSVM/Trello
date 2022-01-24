package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Checklist;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ChecklistRepository implements InterfaceRepository<Checklist>{
    public ChecklistRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO checklists (id, created_by, created_date, name, card_id) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM checklists WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM checklists;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM checklists WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE checklists SET updated_by=?, updated_date=?, name=? WHERE id=?;";

    @Override
    public Checklist findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("ChecklistRepository::findById failed", e);
        }
        throw new IllegalStateException("Checklist with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Checklist> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Checklist> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("ChecklistRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table checklists is empty!");
    }

    @Override
    public void create(Checklist entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setString(4, entity.getName());
            statement.setObject(5, entity.getCardId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Checklist doesn't creates");
        }
    }

    @Override
    public Checklist update(Checklist entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            statement.setString(3, entity.getName());
            statement.setObject(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Checklist with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("ChecklistRepository::delete failed", e);
        }
    }

    private Checklist map(ResultSet rs) throws SQLException {
        Checklist checklist = new Checklist();
        checklist.setId(UUID.fromString(rs.getString("id")));
        checklist.setCreatedBy(rs.getString("created_by"));
        checklist.setUpdatedBy(rs.getString("updated_by"));
        checklist.setCreatedDate(rs.getDate("created_date"));
        checklist.setUpdatedDate(rs.getDate("updated_date"));
        checklist.setName(rs.getString("name"));
        checklist.setCardId(UUID.fromString(rs.getString("card_id")));
        return checklist;
    }
}
