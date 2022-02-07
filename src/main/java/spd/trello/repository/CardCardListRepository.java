package spd.trello.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Card;

import java.util.List;
import java.util.UUID;

@Repository
public class CardCardListRepository {
    public CardCardListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CARD_LIST_ID_STMT = "SELECT * FROM cards WHERE card_list_id=?;";

    public List<Card> findAllByCardListId(UUID boardId) {
        return jdbcTemplate.query(
                FIND_BY_CARD_LIST_ID_STMT,
                new Object[]{boardId},
                new BeanPropertyRowMapper<>(Card.class));
    }
}
