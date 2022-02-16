package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.CheckableItem;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheckableItemRepository extends AbstractRepository<CheckableItem> {
    List<CheckableItem> findAllByChecklistId(UUID checklistId);
}