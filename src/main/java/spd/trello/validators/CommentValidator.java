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
        if (!oldComment.get().getCardId().equals(entity.getCardId())) {
            exceptions.append("Comment cannot be transferred to another card. \n");
        }
        helper.throwException(exceptions);
    }
}
