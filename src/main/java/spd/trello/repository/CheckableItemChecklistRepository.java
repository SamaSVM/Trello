package spd.trello.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.CheckableItem;

import java.util.List;
import java.util.UUID;

@Repository
public class CheckableItemChecklistRepository {
    public CheckableItemChecklistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CHECKLIST_ID_STMT = "SELECT * FROM checkable_items WHERE checklist_id=?;";

    public List<CheckableItem> findAllCheckableItemForChecklist(UUID cardId) {
        return jdbcTemplate.query(
                FIND_BY_CHECKLIST_ID_STMT,
                new Object[]{cardId},
                new BeanPropertyRowMapper<>(CheckableItem.class));
    }
}
