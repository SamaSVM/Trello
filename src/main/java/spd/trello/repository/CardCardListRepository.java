package spd.trello.repository;

import spd.trello.domain.Card;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardCardListRepository {
    public CardCardListRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_CARD_LIST_ID_STMT = "SELECT * FROM cards WHERE card_list_id=?;";

    public List<Card> findAllByCardListId(UUID boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CARD_LIST_ID_STMT)) {
            List<Card> result = new ArrayList<>();
            statement.setObject(1, boardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CardListBoardRepository::findAllByBoardId failed", e);
        }
        throw new IllegalStateException("CardLists for board with ID: " + boardId.toString() + " doesn't exists");
    }

    private Card map(ResultSet rs) throws SQLException {
        Card card = new Card();
        card.setId(UUID.fromString(rs.getString("id")));
        card.setCreatedBy(rs.getString("created_by"));
        card.setUpdatedBy(rs.getString("updated_by"));
        card.setCreatedDate(rs.getDate("created_date"));
        card.setUpdatedDate(rs.getDate("updated_date"));
        card.setName(rs.getString("name"));
        card.setDescription(rs.getString("description"));
        card.setArchived(rs.getBoolean("archived"));
        card.setCardListId(UUID.fromString(rs.getString("card_list_id")));
        return card;
    }
}
