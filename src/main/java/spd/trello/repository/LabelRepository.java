package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Label;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabelRepository extends AbstractRepository<Label> {
    List<Label> findAllByCardId(UUID cardId);
}