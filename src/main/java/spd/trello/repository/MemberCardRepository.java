package spd.trello.repository;

import spd.trello.db.ConnectionPool;
import spd.trello.domain.Member;
import spd.trello.services.MemberService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MemberCardRepository {
    public MemberCardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final MemberService memberService =
            new MemberService(new MemberRepository(ConnectionPool.createDataSource()));

    private final String CREATE_STMT = "INSERT INTO member_card (member_id, card_id) VALUES (?, ?);";

    private final String DELETE_STMT = "DELETE FROM member_card WHERE (member_id=? AND card_id=?);";

    private final String DELETE_BY_CARD_ID_STMT = "DELETE FROM member_card WHERE card_id=?;";

    private final String FIND_BY_IDS_STMT = "SELECT * FROM member_card WHERE (member_id=? AND card_id=?);";

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM member_card WHERE card_id=?;";

    public boolean findByIds(UUID memberId, UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_IDS_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, cardId);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new IllegalStateException("Member-Card link impossible to find!");
        }
    }

    public List<Member> findMembersByCardId(UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CARD_ID_STMT)) {
            List<Member> result = new ArrayList<>();
            statement.setObject(1, cardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(memberService.findById(UUID.fromString(resultSet.getString("member_id"))));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("MemberCardRepository::findByCardId failed", e);
        }
        throw new IllegalStateException("Card with ID: " + cardId.toString() + " doesn't exists");
    }

    public boolean create(UUID memberId, UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, cardId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Member-Card link doesn't creates");
        }
    }

    public boolean delete(UUID memberId, UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, cardId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("MemberCardRepository::delete failed", e);
        }
    }

    public boolean deleteAllMembersForCard(UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_CARD_ID_STMT)) {
            statement.setObject(1, cardId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("MemberWorkspaceRepository::delete failed", e);
        }
    }
}
