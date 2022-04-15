package spd.trello.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import spd.trello.domain.Attachment;
import spd.trello.domain.FileDB;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.AttachmentRepository;
import spd.trello.repository.FileDBRepository;

import java.time.LocalDateTime;
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
        entity.setCreatedDate(LocalDateTime.now());
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public Attachment save(String entity, MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Attachment attachment = objectMapper.readValue(entity, Attachment.class);
            attachment.setName(StringUtils.cleanPath(file.getOriginalFilename()));
            attachment.setType(file.getContentType());
            attachment.setCreatedDate(Date.valueOf(LocalDate.now()));

            FileDB fileDB = new FileDB();
            fileDB.setData(file.getBytes());
            fileDBRepository.save(fileDB);
            attachment.setFileId(fileDB.getId());

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

    @Transactional
    public ResponseEntity<byte[]> getFileResponseEntity(UUID id) {
        Attachment attachment = getById(id);
        FileDB fileDB = fileDBRepository.getById(attachment.getFileId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getName() + "\"")
                .body(fileDB.getData());
    }
}
