package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.Member;
import spd.trello.domain.MemberWorkspace;
import spd.trello.services.MemberService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MemberWorkspaceRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MemberService memberService;

    private final String CREATE_STMT = "INSERT INTO member_workspace (member_id, workspace_id) VALUES (?, ?);";

    private final String DELETE_STMT = "DELETE FROM member_workspace WHERE (member_id=? AND workspace_id=?);";

    private final String DELETE_BY_WORKSPACE_ID_STMT = "DELETE FROM member_workspace WHERE workspace_id=?;";

    private final String FIND_BY_IDS_STMT = "SELECT * FROM member_workspace WHERE (member_id=? AND workspace_id=?);";

    private final String FIND_BY_WORKSPACE_ID_STMT = "SELECT * FROM member_workspace WHERE workspace_id=?;";

    public boolean findByIds(UUID memberId, UUID workspaceId) {
        try {
            List<MemberWorkspace> list = jdbcTemplate.query(
                    FIND_BY_IDS_STMT,
                    new Object[]{memberId, workspaceId},
                    new BeanPropertyRowMapper<>(MemberWorkspace.class));
            return !list.isEmpty();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member-User link impossible to find!");
        }
    }

    public List<Member> findMembersByWorkspaceId(UUID workspaceId) {
        List<Member> result = new ArrayList<>();
        jdbcTemplate.query(
                        FIND_BY_WORKSPACE_ID_STMT,
                        new Object[]{workspaceId},
                        new BeanPropertyRowMapper<>(MemberWorkspace.class))
                .forEach(mc -> result.add(memberService.findById(mc.getMemberId())));

        if (result.isEmpty()) {
            throw new IllegalStateException("Workspace with ID: " + workspaceId.toString() + " doesn't exists");
        }
        return result;
    }

    public boolean create(UUID memberId, UUID workspaceId) {
        try {
            return jdbcTemplate.update(CREATE_STMT, memberId, workspaceId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member-User link doesn't creates");
        }
    }

    public boolean delete(UUID memberId, UUID workspaceId) {
        try {
            return jdbcTemplate.update(DELETE_STMT, memberId, workspaceId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("MemberWorkspaceRepository::delete failed", e);
        }
    }

    public boolean deleteAllMembersForWorkspace(UUID workspaceId) {
        try {
            return jdbcTemplate.update(DELETE_BY_WORKSPACE_ID_STMT, workspaceId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("MemberWorkspaceRepository::delete failed", e);
        }
    }
}
