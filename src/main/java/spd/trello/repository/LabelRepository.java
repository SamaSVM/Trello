package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Label;

import java.util.List;
import java.util.UUID;

@Repository
public class LabelRepository implements InterfaceRepository<Label> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO labels (id, name, card_id) VALUES (?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM labels WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM labels;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM labels WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE labels SET name=? WHERE id=?;";

    @Override
    public Label findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Label.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Label with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Label> findAll() {
        List<Label> result = jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Label.class));
        if (result.isEmpty()) {
            throw new IllegalStateException("Table labels is empty!");
        }
        return result;
    }

    @Override
    public void create(Label entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getName(),
                    entity.getCardId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Label doesn't creates");
        }
    }

    @Override
    public Label update(Label entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getName(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Label with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("LabelRepository::delete failed", e);
        }
    }
}
