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

public class LabelCardRepository {
    public LabelCardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM labels WHERE card_id=?;";

    public List<Label> findAllLabelsForCard(UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CARD_ID_STMT)) {
            List<Label> result = new ArrayList<>();
            statement.setObject(1, cardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("LabelCardRepository::findAllLabelsForCard failed", e);
        }
        throw new IllegalStateException("Labels for card with ID: " + cardId.toString() + " doesn't exists");
    }

    private Label map(ResultSet rs) throws SQLException {
        Label label = new Label();
        label.setId(UUID.fromString(rs.getString("id")));
        label.setName(rs.getString("name"));
        label.setCardId(UUID.fromString(rs.getString("card_id")));
        return label;
    }
}
