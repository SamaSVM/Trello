package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Comment;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CommentCardRepository {
    public CommentCardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM comments WHERE card_id=?;";

    public List<Comment> findAllCommentsForCard(UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CARD_ID_STMT)) {
            List<Comment> result = new ArrayList<>();
            statement.setObject(1, cardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CommentCardRepository::findAllCommentsForCard failed", e);
        }
        throw new IllegalStateException("Comments for card with ID: " + cardId.toString() + " doesn't exists");
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
