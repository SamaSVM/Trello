package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Board;

import java.util.List;
import java.util.UUID;

@Repository
public class BoardRepository implements InterfaceRepository<Board> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT = "INSERT INTO boards " +
            "(id, created_by, created_date, name, description, visibility, favourite, archived, workspace_id)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM boards WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM boards;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM boards WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT = "UPDATE boards SET " +
            "updated_by=?, updated_date=?, name=?, description=?, visibility=?, favourite=?, archived=? WHERE id=?;";

    @Override
    public Board findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Board.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Board with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Board> findAll() {
        return jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Board.class));
    }

    @Override
    public void create(Board entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getVisibility().toString(),
                    entity.getFavourite(),
                    entity.getArchived(),
                    entity.getWorkspaceId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Board doesn't creates");
        }
    }

    @Override
    public Board update(Board entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getVisibility().toString(),
                    entity.getFavourite(),
                    entity.getArchived(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Board with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("BoardRepository::delete failed", e);
        }
    }
}
