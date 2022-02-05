package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CardList;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CardListService extends AbstractService<CardList> {
    public CardListService(InterfaceRepository<CardList> repository, CardListBoardService cardListBoardService) {
        super(repository);
        this.cardListBoardService = cardListBoardService;
    }

    private final CardListBoardService cardListBoardService;

    public CardList create(CardList entity) {
        CardList cardList = new CardList();
        cardList.setId(UUID.randomUUID());
        cardList.setCreatedBy(entity.getCreatedBy());
        cardList.setCreatedDate(Date.valueOf(LocalDate.now()));
        cardList.setName(entity.getName());
        cardList.setBoardId(entity.getBoardId());
        repository.create(cardList);
        return repository.findById(cardList.getId());
    }

    public CardList update(CardList entity) {
        CardList oldCardList = findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldCardList.getName());
        }
        return repository.update(entity);
    }

    public List<CardList> getAllCardListsForBoard(UUID boardId) {
        return cardListBoardService.getAllCardListsForBoard(boardId);
    }
}
