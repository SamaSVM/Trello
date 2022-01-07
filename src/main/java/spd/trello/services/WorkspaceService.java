package spd.trello.services;

import spd.trello.ConnectionPool;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;
import spd.trello.repository.MemberWorkspaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

public class WorkspaceService extends AbstractService<Workspace> {
    private final MemberWorkspaceService memberWorkspaceService =
            new MemberWorkspaceService(new MemberWorkspaceRepository(ConnectionPool.createDataSource()));

    public WorkspaceService(InterfaceRepository<Workspace> repository) {
        super(repository);
    }

    public Workspace findById(UUID id) {
        return repository.findById(id);
    }

    public Workspace create(Member member, String name, String description) {
        Workspace workspace = new Workspace();
        workspace.setId(UUID.randomUUID());
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setCreatedDate(Date.valueOf(LocalDate.now()));
        workspace.setName(name);
        if (description != null) {
            workspace.setDescription(description);
        }
        repository.create(workspace);
        if (!memberWorkspaceService.create(member.getId(), workspace.getId())) {
            repository.delete(workspace.getId());
        }
        return repository.findById(workspace.getId());
    }

    public Workspace update(Member member, Workspace entity) {
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This user cannot update workspace!");
        }
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        memberWorkspaceService.delete(id);
        return repository.delete(id);
    }
}
