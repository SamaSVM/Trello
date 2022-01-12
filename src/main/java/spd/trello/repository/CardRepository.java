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

public class CardRepository implements InterfaceRepository<Card> {
    public CardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO cards (id, created_by, created_date, name, description, archived, card_list_id)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM cards WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM cards;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM cards WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE cards SET updated_by=?, updated_date=?, name=?, description=?, archived=? WHERE id=?;";

    @Override
    public Card findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CardRepository::findCardById failed", e);
        }
        throw new IllegalStateException("Card with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Card> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Card> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CardRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table cards is empty!");
    }

    @Override
    public void create(Card entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setString(4, entity.getName());
            statement.setString(5, entity.getDescription());
            statement.setBoolean(6, entity.getArchived());
            statement.setObject(7, entity.getCardListId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Card doesn't creates");
        }
    }

    @Override
    public Card update(Card entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            statement.setString(3, entity.getName());
            statement.setString(4, entity.getDescription());
            statement.setBoolean(5, entity.getArchived());
            statement.setObject(6, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Card with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("CardRepository::delete failed", e);
        }
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
        card.setAssignedMembers(getMembersForCard(card.getId()));
        card.setLabels(getLabelsForCard(card.getId()));
        card.setAttachments(getAttachmentsForCard(card.getId()));
        card.setComments(getCommentsForCard(card.getId()));
        card.setChecklists(getCheckListsForCard(card.getId()));
        return card;
    }

    private List<Checklist> getCheckListsForCard(UUID cardId) {
        return new ArrayList<>();
    }

    private List<Comment> getCommentsForCard(UUID cardId) {
        return new ArrayList<>();
    }

    private List<Attachment> getAttachmentsForCard(UUID cardId) {
        return new ArrayList<>();
    }

    private List<Label> getLabelsForCard(UUID cardId) {
        return new ArrayList<>();
    }

    private List<Member> getMembersForCard(UUID cardId) {
        return new ArrayList<>();
    }
}
