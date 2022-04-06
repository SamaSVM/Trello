package spd.trello.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spd.trello.domain.Attachment;
import spd.trello.domain.FileDB;
import spd.trello.exeption.BadRequestException;
import spd.trello.services.AttachmentService;

import java.util.UUID;

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
            Attachment result = service.save(resource, file);
            return new ResponseEntity(result, HttpStatus.CREATED);
        }catch (RuntimeException e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadById(@PathVariable UUID id) {
        Attachment attachment = service.getById(id);
        FileDB fileDB = attachment.getFileDB();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getName() + "\"")
                .body(fileDB.getData());
    }
}
