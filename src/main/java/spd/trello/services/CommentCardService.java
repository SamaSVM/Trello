package spd.trello.services;

import spd.trello.domain.Comment;
import spd.trello.repository.CommentCardRepository;

import java.util.List;
import java.util.UUID;

public class CommentCardService {
    public CommentCardService(CommentCardRepository repository) {
        this.repository = repository;
    }

    private final CommentCardRepository repository;

    public List<Comment> getAllCommentsForCard(UUID cardId) {
        return repository.findAllCommentsForCard(cardId);
    }
}
