package spd.trello.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Board;

import java.util.List;
import java.util.UUID;

@Repository
public class BoardWorkspaceRepository {
    public BoardWorkspaceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    private final String FIND_BY_WORKSPACE_ID_STMT = "SELECT * FROM boards WHERE workspace_id=?;";

    public List<Board> findAllByWorkspaceId(UUID workspaceId) {
        return jdbcTemplate.query(
                FIND_BY_WORKSPACE_ID_STMT,
                new Object[]{workspaceId},
                new BeanPropertyRowMapper<>(Board.class));
    }
}
