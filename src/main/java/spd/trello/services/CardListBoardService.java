package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CardList;
import spd.trello.repository.CardListBoardRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CardListBoardService {
    public CardListBoardService(CardListBoardRepository repository) {
        this.repository = repository;
    }

    private final CardListBoardRepository repository;

    public List<CardList> getAllCardListsForBoard(UUID boardId) {
        return repository.findAllByBoardId(boardId);
    }
}
