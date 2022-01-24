package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MemberRepository implements InterfaceRepository<Member> {
    public MemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO members (id, created_by,  created_date,  member_role, user_id) " +
                    "VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM members WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM members;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM members WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE members SET updated_by=?, updated_date=?, member_role=? WHERE id=?;";

    @Override
    public Member findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("MemberRepository::findMemberById failed", e);
        }
        throw new IllegalStateException("Member with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Member> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Member> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("MemberRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table members is empty!");
    }

    @Override
    public void create(Member entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setObject(4, entity.getMemberRole().toString());
            statement.setObject(5, entity.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Member doesn't creates");
        }
    }

    @Override
    public Member update(Member entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            Member oldMember = findById(entity.getId());
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            if (entity.getMemberRole() == null) {
                statement.setString(3, oldMember.getMemberRole().toString());
            } else {
                statement.setString(3, entity.getMemberRole().toString());
            }
            statement.setObject(4, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Member with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("MemberRepository::delete failed", e);
        }
    }

    private Member map(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(UUID.fromString(rs.getString("id")));
        member.setCreatedBy(rs.getString("created_by"));
        member.setUpdatedBy(rs.getString("updated_by"));
        member.setCreatedDate(rs.getDate("created_date"));
        member.setUpdatedDate(rs.getDate("updated_date"));
        member.setMemberRole(MemberRole.valueOf(rs.getString("member_role")));
        member.setUserId(UUID.fromString(rs.getString("user_id")));
        return member;
    }
}
