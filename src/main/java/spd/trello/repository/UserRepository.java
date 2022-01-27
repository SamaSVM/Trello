package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository implements InterfaceRepository<User> {
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String CREATE_STMT =
            "INSERT INTO users (id, first_name, last_name, email, time_zone) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM users WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM users;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM users WHERE id=?;";


    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE users SET first_name=?, last_name=?, email=?, time_zone=? WHERE id=?;";

    @Override
    public User findById(UUID id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_STMT)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return map(resultSet);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("UserRepository::findUserById failed", e);
        }
        throw new IllegalStateException("User with ID: " + id.toString() + " doesn't exists");
    }

    @Override
    public List<User> findAll() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_STMT)) {
            List<User> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("UserRepository::findAll failed", e);
        }
        throw new IllegalStateException("Table users is empty!");
    }

    @Override
    public void create(User entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, entity.getId());
            statement.setString(2, entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getEmail());
            statement.setString(5, entity.getTimeZone());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("User doesn't creates");
        }
    }

    @Override
    public User update(User entity) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ENTITY_STMT)) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getEmail());
            statement.setString(4, entity.getTimeZone());
            statement.setObject(5, entity.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("User with ID: " + entity.getId().toString() + " doesn't updates");
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
            throw new IllegalStateException("UserRepository::delete failed", e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setTimeZone(rs.getString("time_zone"));
        return user;
    }
}
