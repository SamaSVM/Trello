package spd.trello.services;

import spd.trello.domain.Card;
import spd.trello.repository.CardCardListRepository;

import java.util.List;
import java.util.UUID;

public class CardCardListService {
    public CardCardListService(CardCardListRepository repository) {
        this.repository = repository;
    }

    private final CardCardListRepository repository;

    public List<Card> getAllCardsForCardList(UUID cardListId) {
        return repository.findAllByCardListId(cardListId);
    }
}
