package spd.trello.services;

import spd.trello.db.ConnectionPool;
import spd.trello.domain.Board;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.CardListBoardRepository;
import spd.trello.repository.InterfaceRepository;
import spd.trello.repository.MemberBoardRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BoardService extends AbstractService<Board> {
    public BoardService(InterfaceRepository<Board> repository) {
        super(repository);
    }

    private final MemberBoardService memberBoardService =
            new MemberBoardService(new MemberBoardRepository(ConnectionPool.createDataSource()));

    private final CardListBoardService cardListBoardService =
            new CardListBoardService(new CardListBoardRepository(ConnectionPool.createDataSource()));

    public Board create(Member member, UUID workspaceId, String name, String description) {
        Board board = new Board();
        board.setId(UUID.randomUUID());
        board.setCreatedBy(member.getCreatedBy());
        board.setCreatedDate(Date.valueOf(LocalDate.now()));
        board.setName(name);
        if (description != null) {
            board.setDescription(description);
        }
        board.setWorkspaceId(workspaceId);
        repository.create(board);
        if (!memberBoardService.create(member.getId(), board.getId())) {
            delete(board.getId());
        }
        return repository.findById(board.getId());
    }

    public Board update(Member member, Board entity) {
        checkMember(member, entity.getId());
        Board oldBoard = findById(entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldBoard.getName());
        }
        if (entity.getDescription() == null) {
            entity.setDescription(oldBoard.getDescription());
        }
        if (entity.getVisibility() == null) {
            entity.setVisibility(oldBoard.getVisibility());
        }
        if (entity.getFavourite() == null) {
            entity.setFavourite(oldBoard.getFavourite());
        }
        if (entity.getArchived() == null) {
            entity.setArchived(oldBoard.getArchived());
        }
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        memberBoardService.deleteAllMembersForBoard(id);
        return repository.delete(id);
    }

    public boolean addMember(Member member, UUID newMemberId, UUID boardId) {
        checkMember(member, boardId);
        return memberBoardService.create(newMemberId, boardId);
    }

    public boolean deleteMember(Member member, UUID memberId, UUID boardId) {
        checkMember(member, boardId);
        return memberBoardService.delete(memberId, boardId);
    }

    public List<Member> getAllMembers(Member member, UUID workspaceId) {
        checkMember(member, workspaceId);
        return memberBoardService.findMembersByBoardId(workspaceId);
    }

    public List<CardList> getAllCardLists(Member member, UUID boardId) {
        checkMember(member, boardId);
        return cardListBoardService.getAllCardListsForBoard(boardId);
    }

    private void checkMember(Member member, UUID boardId) {
        if (member.getMemberRole() == MemberRole.GUEST ||
                !memberBoardService.findByIds(member.getId(), boardId)) {
            throw new IllegalStateException("This member cannot update board!");
        }
    }
}
