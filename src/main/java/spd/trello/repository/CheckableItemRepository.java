package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.CheckableItem;

import java.util.List;
import java.util.UUID;

@Repository
public class CheckableItemRepository implements InterfaceRepository<CheckableItem>{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO checkable_items (id, name, checked, checklist_id) VALUES (?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM checkable_items WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM checkable_items;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM checkable_items WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE checkable_items SET name=?, checked=? WHERE id=?;";

    @Override
    public CheckableItem findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(CheckableItem.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("CheckableItem with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<CheckableItem> findAll() {
        List<CheckableItem> result = jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(CheckableItem.class));
        if (result.isEmpty()) {
            throw new IllegalStateException("Table checkable_items is empty!");
        }
        return result;
    }

    @Override
    public void create(CheckableItem entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getName(),
                    entity.getChecked(),
                    entity.getChecklistId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CheckableItem doesn't creates");
        }
    }

    @Override
    public CheckableItem update(CheckableItem entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getName(),
                    entity.getChecked(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CheckableItem with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CheckableItemRepository::delete failed", e);
        }
    }
}
