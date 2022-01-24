package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.repository.MemberBoardRepository;

import java.util.List;
import java.util.UUID;

@Service
public class MemberBoardService {
    private final MemberBoardRepository repository;

    public MemberBoardService(MemberBoardRepository repository) {
        this.repository = repository;
    }

    public boolean findByIds(UUID memberId, UUID boardId) {
        return repository.findByIds(memberId, boardId);
    }

    public List<Member> findMembersByBoardId(UUID boardId) {
        return repository.findMembersByBoardId(boardId);
    }

    public boolean create(UUID memberId, UUID boardId) {
        return repository.create(memberId, boardId);
    }

    public boolean deleteAllMembersForBoard(UUID boardId) {
        return repository.deleteAllMembersForBoard(boardId);
    }

    public boolean delete(UUID memberId, UUID boardId) {
        return repository.delete(memberId, boardId);
    }
}
