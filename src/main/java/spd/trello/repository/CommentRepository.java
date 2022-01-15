package spd.trello.repository;

import spd.trello.domain.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommentRepository implements InterfaceRepository<Comment> {
    public CommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO comments (id, created_by, created_date, text, card_id) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM comments WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM comments;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM comments WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE comments SET updated_by=?, updated_date=?, text=? WHERE id=?;";

    @Override
    public Comment findById(UUID id) {
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
        throw new IllegalStateException("Comment with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Comment> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Comment> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CommentRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table comments is empty!");
    }

    @Override
    public void create(Comment entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setString(4, entity.getText());
            statement.setObject(5, entity.getCardId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Comment doesn't creates");
        }
    }

    @Override
    public Comment update(Comment entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            statement.setString(3, entity.getText());
            statement.setObject(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Comment with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("CommentRepository::delete failed", e);
        }
    }

    private Comment map(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setId(UUID.fromString(rs.getString("id")));
        comment.setCreatedBy(rs.getString("created_by"));
        comment.setUpdatedBy(rs.getString("updated_by"));
        comment.setCreatedDate(rs.getDate("created_date"));
        comment.setUpdatedDate(rs.getDate("updated_date"));
        comment.setText(rs.getString("text"));
        comment.setCardId(UUID.fromString(rs.getString("card_id")));
        return comment;
    }
}
