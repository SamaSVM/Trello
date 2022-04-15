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

    public CardListValidator(BoardRepository boardRepository, CardListRepository cardListRepository) {
        this.boardRepository = boardRepository;
        this.cardListRepository = cardListRepository;
    }

    @Override
    public void validateSaveEntity(CardList entity) {
        if (entity.getArchived()) {
            throw new BadRequestException("You cannot create an archived board.");
        }
        if (!boardRepository.existsById(entity.getBoardId())) {
            throw new BadRequestException("The boardId field must belong to a board.");
        }
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(CardList entity) {
        CardList oldCardList = cardListRepository.getById(entity.getId());
        if (!oldCardList.getArchived() && !entity.getArchived()) {
            throw new BadRequestException("Archived CardList cannot be updated.");
        }
        if (!oldCardList.getCreatedBy().equals(entity.getCreatedBy())) {
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if (!oldCardList.getCreatedDate().equals(entity.getCreatedDate())) {
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if (!oldCardList.getBoardId().equals(entity.getBoardId())) {
            throw new BadRequestException("CardList cannot be transferred to another board.");
        }
        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled.");
        }
        if (entity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled.");
        }
        super.validateUpdateEntity(entity);
    }
}
