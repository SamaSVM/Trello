package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.MemberRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@Service
public class MemberService extends AbstractService<Member, MemberRepository> {
    public MemberService(MemberRepository repository, WorkspaceService workspaceService, CardService cardService, BoardService boardService) {
        super(repository);
        this.workspaceService = workspaceService;
        this.cardService = cardService;
        this.boardService = boardService;
    }

    private final WorkspaceService workspaceService;
    private final CardService cardService;
    private final BoardService boardService;

    @Override
    public Member save(Member entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        }catch (RuntimeException e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Member update(Member entity) {
        Member oldMember = getById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldMember.getCreatedBy());
        entity.setCreatedDate(oldMember.getCreatedDate());
        entity.setUserId(oldMember.getUserId());
        if (entity.getMemberRole() == null) {
            entity.setMemberRole(oldMember.getMemberRole());
        }
        try {
            return repository.save(entity);
        }catch (RuntimeException e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        workspaceService.deleteMemberInWorkspaces(id);
        boardService.deleteMemberInBoards(id);
        cardService.deleteMemberInCards(id);
        super.delete(id);
    }


    public void deleteMembersForUser(UUID userId){
        repository.findByUserId(userId).forEach(member -> delete(member.getId()));
    }
}
