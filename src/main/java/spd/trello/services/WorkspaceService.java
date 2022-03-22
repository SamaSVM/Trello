package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Workspace;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
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
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Workspace update(Workspace entity) {
        Workspace oldWorkspace = getById(entity.getId());

        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("Not found updated by!");
        }

        if (entity.getName() == null && entity.getDescription() == null
                && entity.getVisibility().equals(oldWorkspace.getVisibility())
                && entity.getMembersId().equals(oldWorkspace.getMembersId())) {
            throw new ResourceNotFoundException();
        }

        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldWorkspace.getCreatedBy());
        entity.setCreatedDate(oldWorkspace.getCreatedDate());
        if (entity.getName() == null) {
            entity.setName(oldWorkspace.getName());
        }
        if (entity.getDescription() == null && oldWorkspace.getDescription() != null) {
            entity.setDescription(oldWorkspace.getDescription());
        }

        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        boardService.deleteBoardForWorkspace(id);
        super.delete(id);
    }

    public void deleteMemberInWorkspaces(UUID memberId) {
        List<Workspace> workspaces = repository.findAllBymembersIdEquals(memberId);
        for (Workspace workspace : workspaces) {
            Set<UUID> membersId = workspace.getMembersId();
            membersId.remove(memberId);
            if (workspace.getMembersId().isEmpty()) {
                delete(workspace.getId());
            }
        }
    }
}
