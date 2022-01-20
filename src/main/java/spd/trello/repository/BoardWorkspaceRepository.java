package spd.trello.repository;

import spd.trello.domain.Board;
import spd.trello.domain.enums.BoardVisibility;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BoardWorkspaceRepository {
    public BoardWorkspaceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_WORKSPACE_ID_STMT = "SELECT * FROM boards WHERE workspace_id=?;";

    public List<Board> findAllByWorkspaceId(UUID workspaceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_WORKSPACE_ID_STMT)) {
            List<Board> result = new ArrayList<>();
            statement.setObject(1, workspaceId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("BoardWorkspaceRepository::findAllByWorkspaceId failed", e);
        }
        throw new IllegalStateException("Boards for workspace with ID: " + workspaceId.toString() + " doesn't exists");
    }

    private Board map(ResultSet rs) throws SQLException {
        Board board = new Board();
        board.setId(UUID.fromString(rs.getString("id")));
        board.setCreatedBy(rs.getString("created_by"));
        board.setUpdatedBy(rs.getString("updated_by"));
        board.setCreatedDate(rs.getDate("created_date"));
        board.setUpdatedDate(rs.getDate("updated_date"));
        board.setName(rs.getString("name"));
        board.setDescription(rs.getString("description"));
        board.setVisibility(BoardVisibility.valueOf(rs.getString("visibility")));
        board.setFavourite(rs.getBoolean("favourite"));
        board.setArchived(rs.getBoolean("archived"));
        board.setWorkspaceId(UUID.fromString(rs.getString("workspace_id")));
        return board;
    }
}
