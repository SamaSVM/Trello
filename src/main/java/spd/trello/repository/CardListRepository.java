package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.CardList;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardListRepository extends AbstractRepository<CardList> {
    List<CardList> findAllByBoardId(UUID boardId);
}