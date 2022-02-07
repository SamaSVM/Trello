package spd.trello.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import spd.trello.domain.MemberCard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MemberCardRepository {
    public MemberCardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final JdbcTemplate jdbcTemplate;

    private final String CREATE_STMT = "INSERT INTO member_card (member_id, card_id) VALUES (?, ?);";

    private final String DELETE_STMT = "DELETE FROM member_card WHERE (member_id=? AND card_id=?);";

    private final String DELETE_BY_CARD_ID_STMT = "DELETE FROM member_card WHERE card_id=?;";

    private final String FIND_BY_IDS_STMT = "SELECT * FROM member_card WHERE (member_id=? AND card_id=?);";

    private final String FIND_BY_CARD_ID_STMT = "SELECT * FROM member_card WHERE card_id=?;";

    public boolean findByIds(UUID memberId, UUID cardId) {
        try {
            List<MemberCard> list = jdbcTemplate.query
                    (FIND_BY_IDS_STMT, new Object[]{memberId, cardId}, new BeanPropertyRowMapper<>(MemberCard.class));
            return !list.isEmpty();
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member-Card link impossible to find!");
        }
    }

    public List<UUID> findMembersByCardId(UUID cardId) {
        List<UUID> result = new ArrayList<>();
        jdbcTemplate.query(FIND_BY_CARD_ID_STMT, new Object[]{cardId}, new BeanPropertyRowMapper<>(MemberCard.class))
                .forEach(mc -> result.add(mc.getMemberId()));
        return result;
    }

    public boolean create(UUID memberId, UUID cardId) {
        try {
            return jdbcTemplate.update(CREATE_STMT, memberId, cardId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("Member-Card link doesn't creates");
        }
    }

    public boolean delete(UUID memberId, UUID cardId) {
        try {
            return jdbcTemplate.update(DELETE_STMT, memberId, cardId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("MemberCardRepository::delete failed", e);
        }
    }

    public boolean deleteAllMembersForCard(UUID cardId) {
        try {
            return jdbcTemplate.update(DELETE_BY_CARD_ID_STMT, cardId) == 1;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("MemberWorkspaceRepository::delete failed", e);
        }
    }
}
