package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Color;

import java.util.List;
import java.util.UUID;

@Repository
public class ColorRepository implements InterfaceRepository<Color>{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO colors (id, red, green, blue, label_id) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM colors WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM colors;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM colors WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE colors SET red=?, green=?, blue=? WHERE id=?;";

    @Override
    public Color findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Color.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Color with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Color> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Color.class));
    }

    @Override
    public void create(Color entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getRed(),
                    entity.getGreen(),
                    entity.getBlue(),
                    entity.getLabelId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Color doesn't creates");
        }
    }

    @Override
    public Color update(Color entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getRed(),
                    entity.getGreen(),
                    entity.getBlue(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Color with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("ColorRepository::delete failed", e);
        }
    }
}
