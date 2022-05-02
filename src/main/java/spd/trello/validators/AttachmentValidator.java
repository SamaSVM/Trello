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
    private final HelperValidator<Attachment> helper;

    public AttachmentValidator(CardRepository cardRepository, AttachmentRepository attachmentRepository,
                               HelperValidator<Attachment> helper) {
        this.cardRepository = cardRepository;
        this.attachmentRepository = attachmentRepository;
        this.helper = helper;
    }

    @Override

    public void validateSaveEntity(Attachment entity) {
        StringBuilder exceptions = helper.checkCreateEntity(entity);
        checkAttachmentFields(exceptions, entity);
        if (!cardRepository.existsById(entity.getCardId())) {
            exceptions.append("The cardId field must belong to a card. \n");
        }
        helper.throwException(exceptions);
    }

    @Override
    public void validateUpdateEntity(Attachment entity) {
        var oldAttachment = attachmentRepository.findById(entity.getId());
        if (oldAttachment.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent attachment!");
        }
        StringBuilder exceptions = helper.checkUpdateEntity(oldAttachment.get(), entity);
        checkAttachmentFields(exceptions, entity);
        if (!oldAttachment.get().getCardId().equals(entity.getCardId())) {
            exceptions.append("Attachment cannot be transferred to another card. \n");
        }
        helper.throwException(exceptions);
    }

    private void checkAttachmentFields(StringBuilder exceptions, Attachment entity) {
        if (entity.getName() == null) {
            throw new BadRequestException("The name field must be filled.");
        }
        if (entity.getName().length() < 2 || entity.getName().length() > 200) {
            exceptions.append("The name field must be between 2 and 200 characters long. \n");
        }
    }
}
