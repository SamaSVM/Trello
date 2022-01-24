package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.CardList;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CardListBoardRepository {
    public CardListBoardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_BOARD_ID_STMT = "SELECT * FROM card_lists WHERE board_id=?;";

    public List<CardList> findAllByBoardId(UUID boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_BOARD_ID_STMT)) {
            List<CardList> result = new ArrayList<>();
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

    private CardList map(ResultSet rs) throws SQLException {
        CardList cardList = new CardList();
        cardList.setId(UUID.fromString(rs.getString("id")));
        cardList.setCreatedBy(rs.getString("created_by"));
        cardList.setUpdatedBy(rs.getString("updated_by"));
        cardList.setCreatedDate(rs.getDate("created_date"));
        cardList.setUpdatedDate(rs.getDate("updated_date"));
        cardList.setName(rs.getString("name"));
        cardList.setArchived(rs.getBoolean("archived"));
        cardList.setBoardId(UUID.fromString(rs.getString("board_id")));
        return cardList;
    }
}
