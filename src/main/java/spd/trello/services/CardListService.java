package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CardList;
import spd.trello.repository.CardListRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CardListService extends AbstractService<CardList, CardListRepository> {
    public CardListService(CardListRepository repository, CardService cardService) {
        super(repository);
        this.cardService = cardService;
    }

    private final CardService cardService;

    @Override
    public CardList save(CardList entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        return repository.save(entity);
    }

    @Override
    public CardList update(CardList entity) {
        CardList oldCardList = getById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldCardList.getCreatedBy());
        entity.setCreatedDate(oldCardList.getCreatedDate());
        entity.setBoardId(oldCardList.getBoardId());
        if (entity.getName() == null) {
            entity.setName(oldCardList.getName());
        }
        return repository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        cardService.deleteCardsForCardList(id);
        super.delete(id);
    }

    public void deleteCardListsForBoard(UUID boardId) {
        repository.findAllByBoardId(boardId).forEach(cardList -> delete(cardList.getId()));
    }
}
