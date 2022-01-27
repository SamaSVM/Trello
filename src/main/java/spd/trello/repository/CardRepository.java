package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.*;

import java.util.List;
import java.util.UUID;

@Repository
public class CardRepository implements InterfaceRepository<Card> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO cards (id, created_by, created_date, name, description, archived, card_list_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM cards WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM cards;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM cards WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE cards SET updated_by=?, updated_date=?, name=?, description=?, archived=? WHERE id=?;";

    @Override
    public Card findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Card.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Card with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Card> findAll() {
        List<Card> result = jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Card.class));
        if(result.isEmpty()){
            throw new IllegalStateException("Table cards is empty!");
        }
        return result;
    }

    @Override
    public void create(Card entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getArchived(),
                    entity.getCardListId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Card doesn't creates");
        }
    }

    @Override
    public Card update(Card entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getArchived(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Card with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CardRepository::delete failed", e);
        }
    }
}
