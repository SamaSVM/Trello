package spd.trello.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import spd.trello.domain.MemberBoard;
import spd.trello.services.MemberService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MemberBoardRepository {
    public MemberBoardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT = "INSERT INTO member_board (member_id, board_id) VALUES (?, ?);";

    private final String DELETE_BY_BOARD_ID_STMT = "DELETE FROM member_board WHERE board_id=?;";

    private final String DELETE_STMT = "DELETE FROM member_board WHERE (member_id=? AND board_id=?);";

    private final String FIND_BY_IDS_STMT = "SELECT * FROM member_board WHERE (member_id=? AND board_id=?);";

    private final String FIND_BY_BOARD_ID_STMT = "SELECT * FROM member_board WHERE board_id=?;";

    public boolean findByIds(UUID memberId, UUID boardId) {
        try {
            List<MemberBoard> list = jdbcTemplate.query
                    (FIND_BY_IDS_STMT, new Object[]{memberId, boardId}, new BeanPropertyRowMapper<>(MemberBoard.class));
            return !list.isEmpty();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member-Board link impossible to find!");
        }
    }

    public List<UUID> findMembersByBoardId(UUID boardId) {
        List<UUID> result = new ArrayList<>();
        jdbcTemplate.query(FIND_BY_BOARD_ID_STMT, new Object[]{boardId}, new BeanPropertyRowMapper<>(MemberBoard.class))
                .forEach(mb -> result.add(mb.getMemberId()));
        return result;
    }

    public boolean create(UUID memberId, UUID boardId) {
        try {
            return jdbcTemplate.update(CREATE_STMT, memberId, boardId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member-Board link doesn't creates");
        }
    }

    public boolean deleteAllMembersForBoard(UUID boardId) {
        try {
            return jdbcTemplate.update(DELETE_BY_BOARD_ID_STMT, boardId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("BoardWorkspaceRepository::delete failed", e);
        }
    }

    public boolean delete(UUID memberId, UUID boardId) {
        try {
            return jdbcTemplate.update(DELETE_STMT, memberId, boardId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("BoardWorkspaceRepository::delete failed", e);
        }
    }
}
