package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Checklist;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChecklistRepository extends AbstractRepository<Checklist> {
    List<Checklist> findAllByCardId(UUID cardId);
}