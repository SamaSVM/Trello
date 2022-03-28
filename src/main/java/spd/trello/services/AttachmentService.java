package spd.trello.services;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import spd.trello.domain.Attachment;
import spd.trello.domain.FileDB;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.AttachmentRepository;
import spd.trello.repository.FileDBRepository;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class AttachmentService extends AbstractService<Attachment, AttachmentRepository> {
    public AttachmentService(AttachmentRepository repository, FileDBRepository fileDBRepository) {
        super(repository);
        this.fileDBRepository = fileDBRepository;
    }

    private final FileDBRepository fileDBRepository;

    @Override
    public Attachment save(Attachment entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public Attachment save(String entity, MultipartFile file) {
        try {
            JSONObject jsonObject = new JSONObject(entity);
            Attachment attachment = new Attachment();
            attachment.setCreatedBy(jsonObject.getString("createdBy"));
            attachment.setCardId(UUID.fromString(jsonObject.getString("cardId")));
            attachment.setName(jsonObject.getString("name"));
            attachment.setCreatedDate(Date.valueOf(LocalDate.now()));

            FileDB fileDB = new FileDB();
            fileDB.setData(file.getBytes());
            fileDB.setName(StringUtils.cleanPath(file.getOriginalFilename()));
            fileDB.setType(file.getContentType());
            fileDBRepository.save(fileDB);
            attachment.setFileDB(fileDB);

            return repository.save(attachment);
        } catch (RuntimeException | IOException e) {
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

        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
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
