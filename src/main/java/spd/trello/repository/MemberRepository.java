package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Member;

import java.util.List;
import java.util.UUID;

@Repository
public interface MemberRepository extends AbstractRepository<Member> {
    List<Member> findByUserId(UUID userId);
}
