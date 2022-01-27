package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class MemberRepository implements InterfaceRepository<Member> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT =
            "INSERT INTO members (id, created_by,  created_date,  member_role, user_id) " +
                    "VALUES (?, ?, ?, ?, ?);";

    private final String FIND_BY_ID_STMT = "SELECT * FROM members WHERE id=?;";

    private final String FIND_ALL_STMT = "SELECT * FROM members;";

    private final String DELETE_BY_ID_STMT = "DELETE FROM members WHERE id=?;";

    private final String UPDATE_BY_ENTITY_STMT =
            "UPDATE members SET updated_by=?, updated_date=?, member_role=? WHERE id=?;";

    @Override
    public Member findById(UUID id) {
        return jdbcTemplate.query(FIND_BY_ID_STMT, new Object[]{id}, new BeanPropertyRowMapper<>(Member.class))
                .stream().findAny()
                .orElseThrow(() -> new IllegalStateException("Member with ID: " + id.toString() + " doesn't exists"));
    }

    @Override
    public List<Member> findAll() {
        List<Member> result = jdbcTemplate.query(FIND_ALL_STMT, new BeanPropertyRowMapper<>(Member.class));
        if(result.isEmpty()){
            throw new IllegalStateException("Table members is empty!");
        }
        return result;
    }

    @Override
    public void create(Member entity) {
        try {
            jdbcTemplate.update(
                    CREATE_STMT,
                    entity.getId(),
                    entity.getCreatedBy(),
                    entity.getCreatedDate(),
                    entity.getMemberRole().toString(),
                    entity.getUserId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member doesn't creates");
        }
    }

    @Override
    public Member update(Member entity) {
        try {
            jdbcTemplate.update(
                    UPDATE_BY_ENTITY_STMT,
                    entity.getUpdatedBy(),
                    entity.getUpdatedDate(),
                    entity.getMemberRole().toString(),
                    entity.getId());
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member with ID: " + entity.getId().toString() + " doesn't updates");
        }
        return findById(entity.getId());
    }

    @Override
    public boolean delete(UUID id) {
        try {
            return jdbcTemplate.update(DELETE_BY_ID_STMT, id) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("MemberRepository::delete failed", e);
        }
    }
}
