package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Comment;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.CardRepository;
import spd.trello.repository.CommentRepository;

@Component
public class CommentValidator extends AbstractValidator<Comment> {
    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;
    private final HelperValidator<Comment> helper;

    public CommentValidator(CardRepository cardRepository, CommentRepository commentRepository,
                            HelperValidator<Comment> helper) {
        this.cardRepository = cardRepository;
        this.commentRepository = commentRepository;
        this.helper = helper;
    }

    @Override
    public void validateSaveEntity(Comment entity) {
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        checkCommentFields(exceptions, entity);
        if (entity.getCardId() == null) {
            throw new ResourceNotFoundException("Card cannot be null!");
        }
        if (!cardRepository.existsById(entity.getCardId())) {
            exceptions.append("The cardId field must belong to a card. \n");
        }
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Comment entity) {
        var oldComment = commentRepository.findById(entity.getId());
        if (oldComment.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent comment!");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldComment.get(), entity);
        checkCommentFields(exceptions, entity);
        if (!oldComment.get().getCardId().equals(entity.getCardId())) {
            exceptions.append("Comment cannot be transferred to another card. \n");
        }
        helper.throwException(exceptions);
    }

    private void checkCommentFields(StringBuilder exceptions, Comment entity) {
        if (entity.getText() == null) {
            throw new BadRequestException("The text field must be filled.");
        }
        if (entity.getText().length() < 2 || entity.getText().length() > 1000) {
            exceptions.append("The text field must be between 2 and 1000 characters long. \n");
        }
    }
}
