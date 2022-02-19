package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.CardList;
import spd.trello.exeption.BadRequestException;
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
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
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
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
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
