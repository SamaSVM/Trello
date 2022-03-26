package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.*;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends AbstractRepository<Card> {
    List<Card> findAllBymembersIdEquals(UUID memberId);

    List<Card> findAllByCardListId(UUID cardListId);
}
