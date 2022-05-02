package spd.trello.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spd.trello.domain.Workspace;
import spd.trello.repository.WorkspaceRepository;
import spd.trello.validators.WorkspaceValidator;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class WorkspaceService extends AbstractService<Workspace, WorkspaceRepository, WorkspaceValidator> {
    public WorkspaceService(WorkspaceRepository repository, BoardService boardService, WorkspaceValidator validator) {
        super(repository, validator);
        this.boardService = boardService;
    }

    private final BoardService boardService;

    @Override
    public void delete(UUID id) {
        boardService.deleteBoardForWorkspace(id);
        super.delete(id);
    }

    public void deleteMemberInWorkspaces(UUID memberId) {
        log.debug("Cascade delete member in workspaces with id - {}", memberId);
        List<Workspace> workspaces = repository.findAllByMembersIdEquals(memberId);
        for (Workspace workspace : workspaces) {
            Set<UUID> membersId = workspace.getMembersId();
            membersId.remove(memberId);
            if (workspace.getMembersId().isEmpty()) {
                delete(workspace.getId());
            }
        }
    }
}
