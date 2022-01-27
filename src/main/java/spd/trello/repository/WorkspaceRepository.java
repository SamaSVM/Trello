package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Workspace;

import java.util.List;
import java.util.UUID;

@Repository
public class WorkspaceRepository implements InterfaceRepository<Workspace> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO workspaces (id, created_by, created_date, name, description, visibility)" +
                    "VALUES (?, ?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM workspaces WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM workspaces;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM workspaces WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE workspaces SET updated_by=?, updated_date=?, name=?, description=?, visibility=? WHERE id=?;";

    @Override
    public Workspace findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Workspace.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Workspace with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Workspace> findAll() {
        List<Workspace> result = jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Workspace.class));
        if(result.isEmpty()){
            throw new IllegalStateException("Table workspaces is empty!");
        }
        return result;
    }

    @Override
    public void create(Workspace entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getVisibility().toString());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Workspace doesn't creates");
        }
    }

    @Override
    public Workspace update(Workspace entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getVisibility().toString(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Workspace with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("WorkspaceRepository::delete failed", e);
        }
    }
}
