package spd.trello.repository;

import spd.trello.ConnectionPool;
import spd.trello.domain.Board;
import spd.trello.domain.CardList;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.services.MemberBoardService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BoardRepository implements InterfaceRepository<Board> {
    public BoardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final MemberBoardService MBService
            = new MemberBoardService(new MemberBoardRepository(ConnectionPool.createDataSource()));

    private final String CREATE_STMT = "INSERT INTO boards " +
                    "(id, created_by, created_date, name, description, visibility, favourite, archived, workspace_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM boards WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM boards;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM boards WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE boards SET " +
            "updated_by=?, updated_date=?, name=?, description=?, visibility=?, favourite=?, archived=? WHERE id=?;";

    @Override
    public Board findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("BoardRepository::findMemberById failed", e);
        }
        throw new IllegalStateException("Board with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<Board> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<Board> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("BoardRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table boards is empty!");
    }

    public void create(Board entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getCreatedBy());
            statement.setDate(3, entity.getCreatedDate());
            statement.setString(4, entity.getName());
            statement.setString(5, entity.getDescription());
            statement.setString(6, entity.getVisibility().toString());
            statement.setBoolean(7, entity.getFavourite());
            statement.setBoolean(8, entity.getArchived());
            statement.setObject(9, entity.getWorkspaceId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Board doesn't creates");
        }
//        "(id, created_by, created_date, name, description, visibility, favourite, archived, workspace_id)" +
//                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    @Override
    public Board update(Board entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            Board oldBoard = findById(entity.getId());
            statement.setString(1, entity.getUpdatedBy());
            statement.setDate(2, entity.getUpdatedDate());
            if (entity.getName() == null) {
                statement.setString(3, oldBoard.getName());
            } else {
                statement.setString(3, entity.getName());
            }
            if (entity.getDescription() == null) {
                statement.setString(4, oldBoard.getDescription());
            } else {
                statement.setString(4, entity.getDescription());
            }
            if (entity.getVisibility() == null) {
                statement.setString(5, oldBoard.getVisibility().toString());
            } else {
                statement.setString(5, entity.getVisibility().toString());
            }
            if (entity.getFavourite() == null) {
                statement.setBoolean(6, oldBoard.getFavourite());
            } else {
                statement.setBoolean(6, entity.getFavourite());
            }
            if (entity.getArchived() == null) {
                statement.setBoolean(7, oldBoard.getArchived());
            } else {
                statement.setBoolean(7, entity.getArchived());
            }
            statement.setObject(8, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Board with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("BoardRepository::delete failed", e);
        }
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
        board.setCardLists(getCardListsForBoard(board.getId()));
        board.setMembers(MBService.findMembersByBoardId(board.getId()));
        return board;
    }

    private List<CardList> getCardListsForBoard(UUID boardId) {
        return new ArrayList<>();
    }
}
