package spd.trello.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.services.WorkspaceService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/workspace")
public class WorkspaceController extends AbstractController<Workspace, WorkspaceService>{
    public WorkspaceController(WorkspaceService service) {
        super(service);
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<Workspace> create(@PathVariable UUID memberId, @RequestBody Workspace resource) {
        Member member = memberService.findById(memberId);
        Workspace result = service.create(member, resource);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }

    @Override
    @PostMapping
    public ResponseEntity<Workspace> create(@RequestBody Workspace resource) {
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{memberId}/{newMemberId}/{workspaceId}")
    public HttpStatus addMember(
            @PathVariable UUID memberId,
            @PathVariable UUID newMemberId,
            @PathVariable UUID workspaceId) {
        Member member = memberService.findById(memberId);
        service.addMember(member, newMemberId, workspaceId);
        return HttpStatus.OK;
    }

    @DeleteMapping("/{memberId}/{newMemberId}/{workspaceId}")
    public HttpStatus deleteMember(
            @PathVariable UUID memberId,
            @PathVariable UUID newMemberId,
            @PathVariable UUID workspaceId) {
        Member member = memberService.findById(memberId);
        service.deleteMember(member, newMemberId, workspaceId);
        return HttpStatus.OK;
    }

    @GetMapping("/readMembers/{memberId}/{workspaceId}")
    public List<Member> readAllMembersForWorkspace(@PathVariable UUID memberId, @PathVariable UUID workspaceId) {
        Member member = memberService.findById(memberId);
        return service.getAllMembers(member, workspaceId);
    }

    @GetMapping("/readBoards/{memberId}/{workspaceId}")
    public List<Board> readAllBoardsForWorkspace(@PathVariable UUID memberId, @PathVariable UUID workspaceId) {
        Member member = memberService.findById(memberId);
        return service.getAllBoards(member, workspaceId);
    }
}
