package spd.trello.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.CardList;
import spd.trello.services.CardListService;

@RestController
@RequestMapping("/cardlists")
public class CardListController extends AbstractController<CardList, CardListService>{
    public CardListController(CardListService service) {
        super(service);
    }
}
