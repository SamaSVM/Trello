package spd.trello.services;

import spd.trello.db.ConnectionPool;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.BoardWorkspaceRepository;
import spd.trello.repository.InterfaceRepository;
import spd.trello.repository.MemberWorkspaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class WorkspaceService extends AbstractService<Workspace> {
    public WorkspaceService(InterfaceRepository<Workspace> repository) {
        super(repository);
    }

    private final MemberWorkspaceService memberWorkspaceService =
            new MemberWorkspaceService(new MemberWorkspaceRepository(ConnectionPool.createDataSource()));

    private final BoardWorkspaceService boardWorkspaceService =
            new BoardWorkspaceService(new BoardWorkspaceRepository(ConnectionPool.createDataSource()));

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
        checkMember(member, entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        return repository.update(entity);
    }

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
