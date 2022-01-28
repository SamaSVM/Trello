package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.CardList;

import java.util.List;
import java.util.UUID;

@Repository
public class CardListBoardRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String FIND_BY_BOARD_ID_STMT = "SELECT * FROM card_lists WHERE board_id=?;";

    public List<CardList> findAllByBoardId(UUID boardId) {
        List<CardList> result = jdbcTemplate.query(
                FIND_BY_BOARD_ID_STMT,
                new Object[]{boardId},
                new BeanPropertyRowMapper<>(CardList.class));
        if(result.isEmpty()){
            throw new IllegalStateException("CardLists for board with ID: " + boardId.toString() + " doesn't exists");
        }
        return result;
    }
}
