package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Attachment;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.AttachmentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttachmentService extends AbstractService<Attachment, AttachmentRepository> {
    public AttachmentService(AttachmentRepository repository) {
        super(repository);
    }

    @Override
    public Attachment save(Attachment entity) {
        entity.setCreatedDate(LocalDateTime.now());
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Attachment update(Attachment entity) {
        Attachment oldAttachment = getById(entity.getId());

        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("Not found updated by!");
        }

        if (entity.getLink() == null && entity.getName() == null) {
            throw new ResourceNotFoundException();
        }

        entity.setUpdatedDate(LocalDateTime.now());
        entity.setCreatedBy(oldAttachment.getCreatedBy());
        entity.setCreatedDate(oldAttachment.getCreatedDate());
        if (oldAttachment.getCardId() != null) {
            entity.setCardId(oldAttachment.getCardId());
        }

        if (entity.getName() == null) {
            entity.setName(oldAttachment.getName());
        }
        if (entity.getLink() == null) {
            entity.setLink(oldAttachment.getLink());
        }
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void deleteAttachmentsForCard(UUID cardId) {
        repository.findAllByCardId(cardId).forEach(attachment -> delete(attachment.getId()));
    }
}
