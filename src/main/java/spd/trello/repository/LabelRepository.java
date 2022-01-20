package spd.trello.repository;

import spd.trello.domain.Comment;
import spd.trello.domain.Label;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LabelRepository implements InterfaceRepository<Label>{
    public LabelRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO labels (id, name, card_id) VALUES (?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM labels WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM labels;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM labels WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE labels SET name=? WHERE id=?;";

    @Override
    public Label findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("LabelRepository::findById failed", e);
        }
        throw new IllegalStateException("Label with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Label> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Label> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("LabelRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table labels is empty!");
    }

    @Override
    public void create(Label entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getName());
            statement.setObject(3, entity.getCardId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Label doesn't creates");
        }
    }

    @Override
    public Label update(Label entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getName());
            statement.setObject(2, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Label with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("LabelRepository::delete failed", e);
        }
    }

    private Label map(ResultSet rs) throws SQLException {
        Label label = new Label();
        label.setId(UUID.fromString(rs.getString("id")));
        label.setName(rs.getString("name"));
        label.setCardId(UUID.fromString(rs.getString("card_id")));
        return label;
    }
}
