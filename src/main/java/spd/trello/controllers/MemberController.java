package spd.trello.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.services.MemberService;
import spd.trello.services.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/member")
public class MemberController extends AbstractController<Member, MemberService> {
    public MemberController(MemberService service) {
        super(service);
    }

    @Autowired
    UserService userService;

    @Override
    @PutMapping("/{userId}/{id}")
    public ResponseEntity<Member> update(@PathVariable UUID userId, @PathVariable UUID id, @RequestBody Member resource) {
        resource.setId(id);
        User user = userService.findById(userId);
        Member result = service.update(user, resource);
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
