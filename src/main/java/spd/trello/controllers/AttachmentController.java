package spd.trello.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.Attachment;
import spd.trello.services.AttachmentService;

@RestController
@RequestMapping("/attachments")
public class AttachmentController extends AbstractController<Attachment, AttachmentService>{
    public AttachmentController(AttachmentService service) {
        super(service);
    }
}
