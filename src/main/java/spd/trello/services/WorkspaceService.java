package spd.trello.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class WorkspaceService extends AbstractService<Workspace> {
    public WorkspaceService(InterfaceRepository<Workspace> repository) {
        super(repository);
    }

    @Autowired
    private MemberWorkspaceService memberWorkspaceService ;

    @Autowired
    private BoardWorkspaceService boardWorkspaceService ;

    public Workspace create(Member member, Workspace entity) {
        Workspace workspace = new Workspace();
        workspace.setId(UUID.randomUUID());
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setCreatedDate(Date.valueOf(LocalDate.now()));
        workspace.setName(entity.getName());
        if (entity.getDescription() != null) {
            workspace.setDescription(entity.getDescription());
        }
        workspace.setVisibility(entity.getVisibility());
        repository.create(workspace);
        if (!memberWorkspaceService.create(member.getId(), workspace.getId())) {
            repository.delete(workspace.getId());
        }
        return repository.findById(workspace.getId());
    }

    @Override
    public Workspace create(Workspace entity) {
        throw new IllegalStateException("You need to specify who wants to create the workspace!");
    }

    @Override
    public Workspace update(Member member, Workspace entity) {
        checkMember(member, entity.getId());
        Workspace oldWorkspace = findById(entity.getId());
        checkMember(member, entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldWorkspace.getName());
        }
        if (entity.getDescription() == null) {
            entity.setDescription(oldWorkspace.getDescription());
        }
        if (entity.getVisibility() == null) {
            entity.setVisibility(oldWorkspace.getVisibility());
        }
        return repository.update(entity);
    }

    @Override
    public boolean delete(UUID id) {
        memberWorkspaceService.deleteAllMembersForWorkspace(id);
        return repository.delete(id);
    }

    public boolean addMember(Member member, UUID newMemberId, UUID workspaceId) {
        checkMember(member, workspaceId);
        return memberWorkspaceService.create(newMemberId, workspaceId);
    }

    public boolean deleteMember(Member member, UUID memberId, UUID workspaceId) {
        checkMember(member, workspaceId);
        return memberWorkspaceService.delete(memberId, workspaceId);
    }

    public List<Member> getAllMembers(Member member, UUID workspaceId) {
        checkMember(member, workspaceId);
        return memberWorkspaceService.findMembersByWorkspaceId(workspaceId);
    }

    public List<Board> getAllBoards(Member member, UUID workspaceId) {
        checkMember(member, workspaceId);
        return boardWorkspaceService.getAllBoardsForWorkspace(workspaceId);
    }

    private void checkMember(Member member, UUID workspaceId) {
        if (member.getMemberRole() == MemberRole.GUEST ||
                !memberWorkspaceService.findByIds(member.getId(), workspaceId)) {
            throw new IllegalStateException("This member cannot update workspace!");
        }
    }
}
