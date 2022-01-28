package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Comment;
import spd.trello.domain.Label;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class LabelCardRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM labels WHERE card_id=?;";

    public List<Label> findAllLabelsForCard(UUID cardId) {
        List<Label> result = jdbcTemplate.query(
                FIND_BY_CARD_ID_STMT,
                new Object[]{cardId},
                new BeanPropertyRowMapper<>(Label.class));
        if(result.isEmpty()){
            throw new IllegalStateException("Labels for card with ID: " + cardId.toString() + " doesn't exists");
        }
        return result;
    }
}
