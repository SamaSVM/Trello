package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Comment;
import spd.trello.exeption.BadRequestException;
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

    private final AttachmentService attachmentService;

    @Override
    public Comment save(Comment entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

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
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
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
