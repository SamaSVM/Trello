package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Comment;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CardRepository;
import spd.trello.repository.CommentRepository;

@Component
public class CommentValidator extends AbstractValidator<Comment> {
    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;

    public CommentValidator(CardRepository cardRepository, CommentRepository commentRepository) {
        this.cardRepository = cardRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public void validateSaveEntity(Comment entity) {
        if (!cardRepository.existsById(entity.getCardId())) {
            throw new BadRequestException("The cardId field must belong to a card.");
        }
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Comment entity) {
        Comment oldComment = commentRepository.getById(entity.getId());
        if (!oldComment.getCreatedBy().equals(entity.getCreatedBy())) {
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if (!oldComment.getCreatedDate().equals(entity.getCreatedDate())) {
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if (!oldComment.getCardId().equals(entity.getCardId())) {
            throw new BadRequestException("Comment cannot be transferred to another card.");
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
