package spd.trello.controllers;

import org.springframework.web.bind.annotation.*;
import spd.trello.domain.Member;
import spd.trello.services.MemberService;


@RestController
@RequestMapping("/members")
public class MemberController extends AbstractController<Member, MemberService> {
    public MemberController(MemberService service) {
        super(service);
    }
}
