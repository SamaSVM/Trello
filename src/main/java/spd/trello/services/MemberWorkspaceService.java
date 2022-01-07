package spd.trello.services;

import spd.trello.repository.MemberWorkspaceRepository;

import java.util.UUID;

public class MemberWorkspaceService {
    private final MemberWorkspaceRepository repository;

    public MemberWorkspaceService(MemberWorkspaceRepository repository) {
        this.repository = repository;
    }

    public boolean create(UUID memberId, UUID workspaceId) {
        return repository.create(memberId, workspaceId);
    }

    public boolean delete(UUID workspaceId) {
        return repository.delete(workspaceId);
    }
}
