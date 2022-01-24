package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.WorkspaceVisibility;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class WorkspaceRepository implements InterfaceRepository<Workspace> {
    public WorkspaceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO workspaces (id, created_by, created_date, name, description, visibility)" +
                    "VALUES (?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM workspaces WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM workspaces;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM workspaces WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE workspaces SET updated_by=?, updated_date=?, name=?, description=?, visibility=? WHERE id=?;";

    @Override
    public Workspace findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("WorkspaceRepository::findWorkspaceById failed", e);
        }
        throw new IllegalStateException("Workspace with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Workspace> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Workspace> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("WorkspaceRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table workspaces is empty!");
    }

    @Override
    public void create(Workspace entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setString(4, entity.getName());
            statement.setString(5, entity.getDescription());
            statement.setString(6, entity.getVisibility().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Workspace doesn't creates");
        }
    }

    @Override
    public Workspace update(Workspace entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            Workspace oldWorkspace = findById(entity.getId());
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            if (entity.getName() == null) {
                statement.setString(3, oldWorkspace.getName());
            } else {
                statement.setString(3, entity.getName());
            }
            if (entity.getDescription() == null) {
                statement.setString(4, oldWorkspace.getDescription());
            } else {
                statement.setString(4, entity.getDescription());
            }
            if (entity.getVisibility() == null) {
                statement.setString(5, oldWorkspace.getVisibility().toString());
            } else {
                statement.setString(5, entity.getVisibility().toString());
            }
            statement.setObject(6, entity.getId());
            statement.executeUpdate();


        } catch (SQLException e) {
            throw new IllegalStateException("Workspace with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("WorkspaceRepository::delete failed", e);
        }
    }

    private Workspace map(ResultSet rs) throws SQLException {
        Workspace workspace = new Workspace();
        workspace.setId(UUID.fromString(rs.getString("id")));
        workspace.setCreatedBy(rs.getString("created_by"));
        workspace.setUpdatedBy(rs.getString("updated_by"));
        workspace.setCreatedDate(rs.getDate("created_date"));
        workspace.setUpdatedDate(rs.getDate("updated_date"));
        workspace.setName(rs.getString("name"));
        workspace.setDescription(rs.getString("description"));
        workspace.setVisibility(WorkspaceVisibility.valueOf(rs.getString("visibility")));
        return workspace;
    }
}
