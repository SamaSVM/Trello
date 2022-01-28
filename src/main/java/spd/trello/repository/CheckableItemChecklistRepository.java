package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.CheckableItem;

import java.util.List;
import java.util.UUID;

@Repository
public class CheckableItemChecklistRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CHECKLIST_ID_STMT = "SELECT * FROM checkable_items WHERE checklist_id=?;";

    public List<CheckableItem> findAllCheckableItemForChecklist(UUID cardId) {
        List<CheckableItem> result = jdbcTemplate.query(
                FIND_BY_CHECKLIST_ID_STMT,
                new Object[]{cardId},
                new BeanPropertyRowMapper<>(CheckableItem.class));
        if(result.isEmpty()){
            throw new IllegalStateException("CheckableItem for checklist with ID: " + cardId.toString() + " doesn't exists");
        }
        return result;
    }
}
