package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Workspace;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class WorkspaceService extends AbstractService<Workspace> {
    public WorkspaceService(InterfaceRepository<Workspace> repository, MemberWorkspaceService memberWorkspaceService) {
        super(repository);
        this.memberWorkspaceService = memberWorkspaceService;
    }

    private final MemberWorkspaceService memberWorkspaceService ;

    @Override
    public Workspace create(Workspace entity) {
        Workspace workspace = new Workspace();
        workspace.setId(UUID.randomUUID());
        workspace.setCreatedBy(entity.getCreatedBy());
        workspace.setCreatedDate(Date.valueOf(LocalDate.now()));
        workspace.setName(entity.getName());
        if (entity.getDescription() != null) {
            workspace.setDescription(entity.getDescription());
        }
        workspace.setVisibility(entity.getVisibility());
        repository.create(workspace);
        return repository.findById(workspace.getId());
    }

    @Override
    public Workspace update(Workspace entity) {
        Workspace oldWorkspace = findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldWorkspace.getName());
        }
        if (entity.getDescription() == null && oldWorkspace.getDescription() != null) {
            entity.setDescription(oldWorkspace.getDescription());
        }
        if (entity.getVisibility() == null) {
            entity.setVisibility(oldWorkspace.getVisibility());
        }
        return repository.update(entity);
    }

    public boolean addMember(UUID newMemberId, UUID workspaceId) {
        return memberWorkspaceService.create(newMemberId, workspaceId);
    }

    public boolean deleteMember(UUID memberId, UUID workspaceId) {
        return memberWorkspaceService.delete(memberId, workspaceId);
    }
}
