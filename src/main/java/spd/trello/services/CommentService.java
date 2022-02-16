package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Comment;
import spd.trello.repository.CommentRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CommentService extends AbstractService<Comment, CommentRepository> {

    public CommentService(CommentRepository repository, AttachmentService attachmentService) {
        super(repository);
        this.attachmentService = attachmentService;
    }

    @Override
    public Comment save(Comment entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        return repository.save(entity);
    }

    private final AttachmentService attachmentService;

    @Override
    public Comment update(Comment entity) {
        Comment oldCard = getById(entity.getId());
        entity.setCreatedBy(oldCard.getCreatedBy());
        entity.setCreatedDate(oldCard.getCreatedDate());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCardId(oldCard.getCardId());
        if (entity.getText() == null) {
            entity.setText(oldCard.getText());
        }
        return repository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        attachmentService.deleteAttachmentsForComment(id);
        super.delete(id);
    }

    public void deleteCommentsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(comment -> delete(comment.getId()));
    }
}
