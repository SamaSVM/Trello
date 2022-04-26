package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CardList;
import spd.trello.repository.CardListRepository;
import spd.trello.validators.CardListValidator;

import java.util.UUID;

@Service
public class CardListService extends AbstractService<CardList, CardListRepository, CardListValidator> {
    public CardListService(CardListRepository repository, CardService cardService, CardListValidator validator) {
        super(repository, validator);
        this.cardService = cardService;
    }

    private final CardService cardService;

    @Override
    public void delete(UUID id) {
        cardService.deleteCardsForCardList(id);
        super.delete(id);
    }

    public void deleteCardListsForBoard(UUID boardId) {
        repository.findAllByBoardId(boardId).forEach(cardList -> delete(cardList.getId()));
    }
}
