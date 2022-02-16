package spd.trello.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.repository.WorkspaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class WorkspaceService extends AbstractService<Workspace, WorkspaceRepository> {
    public WorkspaceService(WorkspaceRepository repository, BoardService boardService) {
        super(repository);
        this.boardService = boardService;
    }

    private final BoardService boardService;

    @Override
    public Workspace save(Workspace entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        return repository.save(entity);
    }

    @Override
    public Workspace update(Workspace entity) {
        Workspace oldWorkspace = getById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldWorkspace.getCreatedBy());
        entity.setCreatedDate(oldWorkspace.getCreatedDate());
        if (entity.getName() == null) {
            entity.setName(oldWorkspace.getName());
        }
        if (entity.getDescription() == null && oldWorkspace.getDescription() != null) {
            entity.setDescription(oldWorkspace.getDescription());
        }
        if (entity.getVisibility() == null) {
            entity.setVisibility(oldWorkspace.getVisibility());
        }
        return repository.save(entity);
    }

    @Override
    public void delete(UUID id) {
        boardService.deleteBoardForWorkspace(id);
        super.delete(id);
    }

    public void deleteMemberInWorkspaces(UUID memberId) {
        List<Workspace> workspaces = repository.findAllByMembersIdsEquals(memberId);
        for (Workspace workspace : workspaces) {
            Set<UUID> membersIds = workspace.getMembersIds();
            membersIds.remove(memberId);
            if (workspace.getMembersIds().isEmpty()){
                delete(workspace.getId());
            }
        }
    }
}
