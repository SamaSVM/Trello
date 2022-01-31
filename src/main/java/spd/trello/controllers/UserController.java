package spd.trello.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.User;
import spd.trello.services.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController extends AbstractController<User, UserService> {
    public UserController(UserService service) {
        super(service);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable UUID id, @RequestBody User resource) {
        resource.setId(id);
        User result = service.update(resource);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @Override
    @PutMapping("/{memberId}/{id}")
    public ResponseEntity<User> update(@PathVariable UUID memberId, @PathVariable UUID id, @RequestBody User resource) {
        return new ResponseEntity( HttpStatus.BAD_REQUEST);
    }
}
