package spd.trello.services;

import spd.trello.domain.Member;
import spd.trello.repository.MemberBoardRepository;
import spd.trello.repository.MemberWorkspaceRepository;

import java.util.List;
import java.util.UUID;

public class MemberBoardService {
    private final MemberBoardRepository repository;

    public MemberBoardService(MemberBoardRepository repository) {
        this.repository = repository;
    }

    public boolean findByIds(UUID memberId, UUID workspaceId) {
        return repository.findByIds(memberId, workspaceId);
    }

    public List<Member> findMembersByBoardId(UUID workspaceId) {
        return repository.findMembersByBoardId(workspaceId);
    }

    public boolean create(UUID memberId, UUID boardId) {
        return repository.create(memberId, boardId);
    }

    public boolean delete(UUID boardId) {
        return repository.delete(boardId);
    }
}
