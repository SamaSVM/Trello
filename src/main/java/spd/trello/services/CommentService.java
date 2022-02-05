package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Comment;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService extends AbstractService<Comment> {
    public CommentService(InterfaceRepository<Comment> repository, CommentCardService commentCardService) {
        super(repository);
        this.commentCardService = commentCardService;
    }

    private final CommentCardService commentCardService;

    @Override
    public Comment create(Comment entity) {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setCreatedBy(entity.getCreatedBy());
        comment.setCreatedDate(Date.valueOf(LocalDate.now()));
        comment.setText(entity.getText());
        comment.setCardId(entity.getCardId());
        repository.create(comment);
        return repository.findById(comment.getId());
    }

    @Override
    public Comment update(Comment entity) {
        Comment oldCard = repository.findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getText() == null) {
            entity.setText(oldCard.getText());
        }
        return repository.update(entity);
    }

    public List<Comment> getAllCommentsForCard(UUID cardId) {
        return commentCardService.getAllCommentsForCard(cardId);
    }
}
