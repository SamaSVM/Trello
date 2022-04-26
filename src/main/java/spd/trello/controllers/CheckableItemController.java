package spd.trello.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.CheckableItem;
import spd.trello.services.CheckableItemService;

@RestController
@RequestMapping("/checkableitems")
public class CheckableItemController extends AbstractController<CheckableItem, CheckableItemService> {
    public CheckableItemController(CheckableItemService service) {
        super(service);
    }
}
