package spd.trello.repository;

import org.springframework.stereotype.Repository;
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

@Repository
public class MemberWorkspaceRepository {
    public MemberWorkspaceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

            private final MemberService memberService = new MemberService(new MemberRepository(ConnectionPool.createDataSource()));

    private final String CREATE_STMT = "INSERT INTO member_workspace (member_id, workspace_id) VALUES (?, ?);";

    private final String DELETE_STMT = "DELETE FROM member_workspace WHERE (member_id=? AND workspace_id=?);";

    private final String DELETE_BY_WORKSPACE_ID_STMT = "DELETE FROM member_workspace WHERE workspace_id=?;";

    private final String FIND_BY_IDS_STMT = "SELECT * FROM member_workspace WHERE (member_id=? AND workspace_id=?);";

    private final String FIND_BY_WORKSPACE_ID_STMT = "SELECT * FROM member_workspace WHERE workspace_id=?;";

    public boolean findByIds(UUID memberId, UUID workspaceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_IDS_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, workspaceId);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new IllegalStateException("Member-User link impossible to find!");
        }
    }

    public List<Member> findMembersByWorkspaceId(UUID workspaceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_WORKSPACE_ID_STMT)) {
            List<Member> result = new ArrayList<>();
            statement.setObject(1, workspaceId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(memberService.findById(UUID.fromString(resultSet.getString("member_id"))));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("MemberWorkspaceRepository::findByWorkspaceId failed", e);
        }
        throw new IllegalStateException("Workspace with ID: " + workspaceId.toString() + " doesn't exists");
    }

    public boolean create(UUID memberId, UUID workspaceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, workspaceId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Member-User link doesn't creates");
        }
    }

    public boolean delete(UUID memberId, UUID workspaceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, workspaceId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("MemberWorkspaceRepository::delete failed", e);
        }
    }

    public boolean deleteAllMembersForWorkspace(UUID workspaceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_WORKSPACE_ID_STMT)) {
            statement.setObject(1, workspaceId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("MemberWorkspaceRepository::delete failed", e);
        }
    }
}
