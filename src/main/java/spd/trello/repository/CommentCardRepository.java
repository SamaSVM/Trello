package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Comment;

import java.util.List;
import java.util.UUID;

@Repository
public class CommentCardRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM comments WHERE card_id=?;";

    public List<Comment> findAllCommentsForCard(UUID cardId) {
        List<Comment> result = jdbcTemplate.query(
                FIND_BY_CARD_ID_STMT,
                new Object[]{cardId},
                new BeanPropertyRowMapper<>(Comment.class));
        if(result.isEmpty()){
            throw new IllegalStateException("Comments for card with ID: " + cardId.toString() + " doesn't exists");
        }
        return result;
    }
}
