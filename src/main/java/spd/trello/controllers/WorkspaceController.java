package spd.trello.controllers;

import org.springframework.web.bind.annotation.*;
import spd.trello.domain.Workspace;
import spd.trello.services.WorkspaceService;

@RestController
@RequestMapping("/workspaces")
public class WorkspaceController extends AbstractController<Workspace, WorkspaceService>{
    public WorkspaceController(WorkspaceService service) {
        super(service);
    }
}
