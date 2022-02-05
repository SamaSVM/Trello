package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.User;

import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository implements InterfaceRepository<User> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO users (id, first_name, last_name, email, time_zone) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM users WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM users;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM users WHERE id=?;";


    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE users SET first_name=?, last_name=?, email=?, time_zone=? WHERE id=?;";

    @Override
    public User findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(User.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("User with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(User.class));
    }

    @Override
    public void create(User entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getEmail(),
                    entity.getTimeZone());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("User doesn't creates");
        }
    }

    @Override
    public User update(User entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getFirstName(),
                    entity.getLastName(),
                    entity.getEmail(),
                    entity.getTimeZone(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("User with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("UserRepository::delete failed", e);
        }
    }
}
