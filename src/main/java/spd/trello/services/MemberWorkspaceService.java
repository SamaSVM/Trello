package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.repository.MemberWorkspaceRepository;

import java.util.List;
import java.util.UUID;

@Service
public class MemberWorkspaceService {
    private final MemberWorkspaceRepository repository;

    public MemberWorkspaceService(MemberWorkspaceRepository repository) {
        this.repository = repository;
    }

    public boolean findByIds(UUID memberId, UUID workspaceId) {
        return repository.findByIds(memberId, workspaceId);
    }

    public List<Member> findMembersByWorkspaceId(UUID workspaceId) {
        return repository.findMembersByWorkspaceId(workspaceId);
    }

    public boolean create(UUID memberId, UUID workspaceId) {
        return repository.create(memberId, workspaceId);
    }

    public boolean delete(UUID memberId, UUID workspaceId) {
        return repository.delete(memberId, workspaceId);
    }

    public boolean deleteAllMembersForWorkspace(UUID workspaceId) {
        return repository.deleteAllMembersForWorkspace(workspaceId);
    }
}

