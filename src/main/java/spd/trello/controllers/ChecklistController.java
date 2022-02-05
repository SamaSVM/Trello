package spd.trello.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.Checklist;
import spd.trello.services.ChecklistService;

@RestController
@RequestMapping("/checklists")
public class ChecklistController extends AbstractController<Checklist, ChecklistService> {
    public ChecklistController(ChecklistService service) {
        super(service);
    }
}
