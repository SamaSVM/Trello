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
public class CommentRepository implements InterfaceRepository<Comment> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO comments (id, created_by, created_date, text, card_id) VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM comments WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM comments;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM comments WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE comments SET updated_by=?, updated_date=?, text=? WHERE id=?;";

    @Override
    public Comment findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Comment.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Comment with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Comment> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Comment.class));
    }

    @Override
    public void create(Comment entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getText(),
                    entity.getCardId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Comment doesn't creates");
        }
    }

    @Override
    public Comment update(Comment entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getText(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Comment with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("CommentRepository::delete failed", e);
        }
    }
}
