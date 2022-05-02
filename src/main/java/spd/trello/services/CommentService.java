package spd.trello.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spd.trello.domain.Comment;
import spd.trello.repository.CommentRepository;
import spd.trello.validators.CommentValidator;

import java.util.UUID;

@Slf4j
@Service
public class CommentService extends AbstractService<Comment, CommentRepository, CommentValidator> {

    public CommentService(CommentRepository repository, CommentValidator validator) {
        super(repository, validator);
    }

    public void deleteCommentsForCard(UUID cardId) {
        log.debug("Cascade delete comments for card with id - {}", cardId);
        repository.findAllByCardId(cardId).forEach(comment -> delete(comment.getId()));
    }
}
