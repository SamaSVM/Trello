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
import spd.trello.validators.AttachmentValidator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttachmentService extends AbstractService<Attachment, AttachmentRepository, AttachmentValidator> {
    public AttachmentService
            (AttachmentRepository repository, FileDBRepository fileDBRepository, AttachmentValidator validator) {
        super(repository, validator);
        this.fileDBRepository = fileDBRepository;
    }

    private final FileDBRepository fileDBRepository;

    public Attachment save(String entity, MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Attachment attachment = objectMapper.readValue(entity, Attachment.class);
            attachment.setName(StringUtils.cleanPath(file.getOriginalFilename()));
            attachment.setType(file.getContentType());

            FileDB fileDB = new FileDB();
            fileDB.setData(file.getBytes());
            fileDBRepository.save(fileDB);
            attachment.setFileId(fileDB.getId());

            return repository.save(attachment);
        } catch (RuntimeException | IOException e) {
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
