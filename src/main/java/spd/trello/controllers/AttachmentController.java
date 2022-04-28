package spd.trello.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spd.trello.domain.Attachment;
import spd.trello.exeption.BadRequestException;
import spd.trello.services.AttachmentService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/attachments")
public class AttachmentController extends AbstractController<Attachment, AttachmentService> {
    public AttachmentController(AttachmentService service) {
        super(service);
    }

    @PostMapping("/upload")
    public ResponseEntity<Attachment> create
            (@RequestParam("resource") String resource, @RequestParam("file") MultipartFile file) {
        try {
            log.debug("Upload attachment with file.");
            Attachment result = service.save(resource, file);
            return new ResponseEntity(result, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.debug("Failed to upload file.");
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadById(@PathVariable UUID id) {
        return service.getFileResponseEntity(id);
    }
}
