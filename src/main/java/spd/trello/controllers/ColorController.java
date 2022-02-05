package spd.trello.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spd.trello.domain.Color;
import spd.trello.services.ColorService;

@RestController
@RequestMapping("/colors")
public class ColorController extends AbstractController<Color, ColorService>{
    public ColorController(ColorService service) {
        super(service);
    }
}
