package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.CardList;

import java.util.List;
import java.util.UUID;

@Repository
public class CardListRepository implements InterfaceRepository<CardList> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT = "INSERT INTO card_lists " +
            "(id, created_by, created_date, name, archived, board_id) VALUES (?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM card_lists WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM card_lists;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM card_lists WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE card_lists SET " +
            "updated_by=?, updated_date=?, name=?, archived=? WHERE id=?;";

    @Override
    public CardList findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(CardList.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("CardList with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<CardList> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(CardList.class));
    }

    @Override
    public void create(CardList entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getName(),
                    entity.getArchived(),
                    entity.getBoardId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CardList doesn't creates");
        }
    }

    @Override
    public CardList update(CardList entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getName(),
                    entity.getArchived(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CardList with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CardListRepository::delete failed", e);
        }
    }
}
