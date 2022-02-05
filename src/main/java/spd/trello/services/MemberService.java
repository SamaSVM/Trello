package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService extends AbstractService<Member> {
    public MemberService(InterfaceRepository<Member> repository, MemberWorkspaceService memberWorkspaceService, MemberBoardService memberBoardService, MemberCardService memberCardService) {
        super(repository);
        this.memberWorkspaceService = memberWorkspaceService;
        this.memberBoardService = memberBoardService;
        this.memberCardService = memberCardService;
    }

    private final MemberWorkspaceService memberWorkspaceService;
    private final MemberBoardService memberBoardService;
    private final MemberCardService memberCardService;

    @Override
    public Member create(Member entity) {
        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setCreatedBy(entity.getCreatedBy());
        member.setCreatedDate(Date.valueOf(LocalDate.now()));
        member.setUserId(entity.getUserId());
        member.setMemberRole(entity.getMemberRole());
        repository.create(member);
        return repository.findById(member.getId());
    }

    @Override
    public Member update(Member entity) {
        Member oldMember = findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getMemberRole() == null) {
            entity.setMemberRole(oldMember.getMemberRole());
        }
        return repository.update(entity);
    }

        public List<Member> getAllMembersForWorkspace(UUID workspaceId) {
        return memberWorkspaceService.findMembersByWorkspaceId(workspaceId);
    }

    public List<Member> getAllMembersForBoard(UUID boardId) {
        return memberBoardService.findMembersByBoardId(boardId);
    }

        public List<Member> getAllMembersForCard(UUID cardId) {
        return memberCardService.findMembersByCardId(cardId);
    }
}
