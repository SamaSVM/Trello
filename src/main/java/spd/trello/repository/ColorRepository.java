package spd.trello.repository;

import spd.trello.domain.Color;
import spd.trello.domain.Comment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ColorRepository implements InterfaceRepository<Color>{
    public ColorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO colors (id, red, green, blue, label_id) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM colors WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM colors;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM colors WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE colors SET red=?, green=?, blue=? WHERE id=?;";

    @Override
    public Color findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CommentRepository::findCardById failed", e);
        }
        throw new IllegalStateException("Color with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Color> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Color> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("ColorRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table colors is empty!");
    }

    @Override
    public void create(Color entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setInt(2, entity.getRed());
            statement.setInt(3, entity.getGreen());
            statement.setInt(4, entity.getBlue());
            statement.setObject(5, entity.getLabelId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Color doesn't creates");
        }
    }

    @Override
    public Color update(Color entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setInt(1, entity.getRed());
            statement.setInt(2, entity.getGreen());
            statement.setInt(3, entity.getBlue());
            statement.setObject(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Color with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("ColorRepository::delete failed", e);
        }
    }

    private Color map(ResultSet rs) throws SQLException {
        Color color = new Color();
        color.setId(UUID.fromString(rs.getString("id")));
        color.setRed(rs.getInt("red"));
        color.setGreen(rs.getInt("green"));
        color.setBlue(rs.getInt("blue"));
        color.setLabelId(UUID.fromString(rs.getString("label_id")));
        return color;
    }
}
