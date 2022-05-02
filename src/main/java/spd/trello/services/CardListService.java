package spd.trello.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spd.trello.domain.CardList;
import spd.trello.repository.CardListRepository;
import spd.trello.validators.CardListValidator;

import java.util.UUID;

@Slf4j
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
        log.debug("Cascade delete card list for boar with id - {}", boardId);
        repository.findAllByBoardId(boardId).forEach(cardList -> delete(cardList.getId()));
    }
}
