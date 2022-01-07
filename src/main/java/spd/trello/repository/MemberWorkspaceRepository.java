package spd.trello.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MemberWorkspaceRepository {
    private final DataSource dataSource;

    public MemberWorkspaceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final String CREATE_STMT = "INSERT INTO member_workspace (member_id, workspace_id) VALUES (?, ?);";

    private final String DELETE_BY_WORKSPACE_ID_STMT = "DELETE FROM member_workspace WHERE workspace_id=?;";

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

    public boolean delete(UUID workspaceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_WORKSPACE_ID_STMT)) {
            statement.setObject(1, workspaceId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("MEMBERWorkspaceRepository::delete failed", e);
        }
    }
}
