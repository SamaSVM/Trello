package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Comment;
import spd.trello.repository.CommentRepository;
import spd.trello.validators.CommentValidator;

import java.util.UUID;

@Service
public class CommentService extends AbstractService<Comment, CommentRepository, CommentValidator> {

    public CommentService(CommentRepository repository, CommentValidator validator) {
        super(repository, validator);
    }

    public void deleteCommentsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(comment -> delete(comment.getId()));
    }
}
