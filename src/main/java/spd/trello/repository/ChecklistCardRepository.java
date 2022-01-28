package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Checklist;

import java.util.List;
import java.util.UUID;

@Repository
public class ChecklistCardRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM checklists WHERE card_id=?;";

    public List<Checklist> findAllChecklistForCard(UUID cardId) {
        List<Checklist> result = jdbcTemplate.query(
                FIND_BY_CARD_ID_STMT,
                new Object[]{cardId},
                new BeanPropertyRowMapper<>(Checklist.class));
        if(result.isEmpty()){
            throw new IllegalStateException("Checklist for card with ID: " + cardId.toString() + " doesn't exists");
        }
        return result;
    }
}
