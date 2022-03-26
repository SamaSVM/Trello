package spd.trello.controllers;

import org.springframework.web.bind.annotation.*;
import spd.trello.domain.User;
import spd.trello.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController extends AbstractController<User, UserService> {
    public UserController(UserService service) {
        super(service);
    }
}
