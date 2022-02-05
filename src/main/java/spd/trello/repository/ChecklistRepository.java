package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Checklist;

import java.util.List;
import java.util.UUID;

@Repository
public class ChecklistRepository implements InterfaceRepository<Checklist>{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO checklists (id, created_by, created_date, name, card_id) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM checklists WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM checklists;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM checklists WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE checklists SET updated_by=?, updated_date=?, name=? WHERE id=?;";

    @Override
    public Checklist findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Checklist.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Checklist with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Checklist> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Checklist.class));
    }

    @Override
    public void create(Checklist entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getName(),
                    entity.getCardId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Checklist doesn't creates");
        }
    }

    @Override
    public Checklist update(Checklist entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getName(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Checklist with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("ChecklistRepository::delete failed", e);
        }
    }
}
