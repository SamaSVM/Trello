package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Attachment;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.AttachmentRepository;
import spd.trello.repository.CardRepository;

@Component
public class AttachmentValidator extends AbstractValidator<Attachment> {
    private final CardRepository cardRepository;
    private final AttachmentRepository attachmentRepository;

    public AttachmentValidator(CardRepository cardRepository, AttachmentRepository attachmentRepository) {
        this.cardRepository = cardRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override

    public void validateSaveEntity(Attachment entity) {
        if (!cardRepository.existsById(entity.getCardId())) {
            throw new BadRequestException("The cardId field must belong to a card.");
        }

        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(Attachment entity) {
        Attachment oldAttachment = attachmentRepository.getById(entity.getId());
        if(!oldAttachment.getCreatedBy().equals(entity.getCreatedBy())){
            throw new BadRequestException("The createdBy field cannot be updated.");
        }
        if(!oldAttachment.getCreatedDate().equals(entity.getCreatedDate())){
            throw new BadRequestException("The createdDate field cannot be updated.");
        }
        if(!oldAttachment.getCardId().equals(entity.getCardId())){
            throw new BadRequestException("Attachment cannot be transferred to another card.");
        }
        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("The updatedBy field must be filled.");
        }
        if (entity.getUpdatedDate() == null) {
            throw new BadRequestException("The updatedDate field must be filled.");
        }
        super.validateUpdateEntity(entity);
    }
}
