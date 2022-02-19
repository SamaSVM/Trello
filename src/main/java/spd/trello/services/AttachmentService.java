package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Attachment;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.AttachmentRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class AttachmentService extends AbstractService<Attachment, AttachmentRepository> {
    public AttachmentService(AttachmentRepository repository) {
        super(repository);
    }

    @Override
    public Attachment save(Attachment entity) {
        if ((entity.getCardId() != null && entity.getCommentId() != null) ||
                (entity.getCardId() == null && entity.getCommentId() == null)) {
            throw new IllegalStateException();
        }
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Attachment update(Attachment entity) {
        Attachment oldAttachment = getById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldAttachment.getCreatedBy());
        entity.setCreatedDate(oldAttachment.getCreatedDate());
        if(oldAttachment.getCardId() != null) {
            entity.setCardId(oldAttachment.getCardId());
        }
        if(oldAttachment.getCommentId() != null) {
            entity.setCommentId(oldAttachment.getCommentId());
        }
        if(entity.getName() == null) {
            entity.setName(oldAttachment.getName());
        }
        if(entity.getLink() == null) {
            entity.setLink(oldAttachment.getLink());
        }
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void deleteAttachmentsForComment(UUID commentId) {
        repository.findAllByCommentId(commentId).forEach(attachment -> delete(attachment.getId()));
    }

    public void deleteAttachmentsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(attachment -> delete(attachment.getId()));
    }
}
