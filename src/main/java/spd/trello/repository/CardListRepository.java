package spd.trello.repository;

import spd.trello.domain.CardList;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardListRepository implements InterfaceRepository<CardList> {
    public CardListRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT = "INSERT INTO card_lists " +
            "(id, created_by, created_date, name, archived, board_id) VALUES (?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM card_lists WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM card_lists;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM card_lists WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE card_lists SET " +
            "updated_by=?, updated_date=?, name=?, archived=? WHERE id=?;";

    @Override
    public CardList findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CardListRepository::findCardListById failed", e);
        }
        throw new IllegalStateException("CardList with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<CardList> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<CardList> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CardListRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table card_lists is empty!");
    }

    @Override
    public void create(CardList entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setString(4, entity.getName());
            statement.setBoolean(5, entity.getArchived());
            statement.setObject(6, entity.getBoardId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("CardList doesn't creates");
        }
    }

    @Override
    public CardList update(CardList entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            statement.setString(3, entity.getName());
            statement.setBoolean(4, entity.getArchived());
            statement.setObject(5, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("CardList with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("CardListRepository::delete failed", e);
        }
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
