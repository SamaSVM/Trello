package spd.trello.repository;

import spd.trello.domain.Checklist;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ChecklistCardRepository {
    public ChecklistCardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM checklists WHERE card_id=?;";

    public List<Checklist> findAllChecklistForCard(UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CARD_ID_STMT)) {
            List<Checklist> result = new ArrayList<>();
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
