package spd.trello.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.Comment;
import spd.trello.services.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentController extends AbstractController<Comment, CommentService> {
    public CommentController(CommentService service) {
        super(service);
    }
}
