package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Comment;
import spd.trello.repository.CommentCardRepository;

import java.util.List;
import java.util.UUID;

@Service
public class CommentCardService {
    public CommentCardService(CommentCardRepository repository) {
        this.repository = repository;
    }

    private final CommentCardRepository repository;

    public List<Comment> getAllCommentsForCard(UUID cardId) {
        return repository.findAllCommentsForCard(cardId);
    }
}
