package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.CardList;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.CardListRepository;

@Component
public class CardListValidator extends AbstractValidator<CardList> {

    private final BoardRepository boardRepository;
    private final CardListRepository cardListRepository;
    private final HelperValidator<CardList> helper;

    public CardListValidator(BoardRepository boardRepository, CardListRepository cardListRepository,
                             HelperValidator<CardList> helper) {
        this.boardRepository = boardRepository;
        this.cardListRepository = cardListRepository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(CardList entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived card list.");
        }
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        if (!boardRepository.existsById(entity.getBoardId())) {
            exceptions.append("The boardId field must belong to a board.");
        }
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(CardList entity) {
        var oldCardList = cardListRepository.findById(entity.getId());
        if (oldCardList.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent workspace!");
        }
        if (!oldCardList.get().getArchived() && !entity.getArchived()) {
            throw new BadRequestException("Archived CardList cannot be updated.");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldCardList.get(), entity);
        if (!oldCardList.get().getBoardId().equals(entity.getBoardId())) {
            throw new BadRequestException("CardList cannot be transferred to another board.");
        }
        helper.throwException(exceptions);
    }
}
