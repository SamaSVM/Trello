package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.CheckableItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CheckableItemRepository implements InterfaceRepository<CheckableItem>{
    public CheckableItemRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO checkable_items (id, name, checked, checklist_id) VALUES (?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM checkable_items WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM checkable_items;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM checkable_items WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE checkable_items SET name=?, checked=? WHERE id=?;";

    @Override
    public CheckableItem findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CheckableItemRepository::findById failed", e);
        }
        throw new IllegalStateException("CheckableItem with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<CheckableItem> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<CheckableItem> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CheckableItemRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table checkable_items is empty!");
    }

    @Override
    public void create(CheckableItem entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getName());
            statement.setBoolean(3, entity.getChecked());
            statement.setObject(4, entity.getChecklistId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("CheckableItem doesn't creates");
        }
    }

    @Override
    public CheckableItem update(CheckableItem entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getName());
            statement.setBoolean(2, entity.getChecked());
            statement.setObject(3, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("CheckableItem with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("CheckableItemRepository::delete failed", e);
        }
    }

    private CheckableItem map(ResultSet rs) throws SQLException {
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setId(UUID.fromString(rs.getString("id")));
        checkableItem.setName(rs.getString("name"));
        checkableItem.setChecked(rs.getBoolean("checked"));
        checkableItem.setChecklistId(UUID.fromString(rs.getString("checklist_id")));
        return checkableItem;
    }
}
