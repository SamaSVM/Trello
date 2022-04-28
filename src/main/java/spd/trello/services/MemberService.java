package spd.trello.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.repository.MemberRepository;
import spd.trello.validators.MemberValidator;

import java.util.UUID;

@Slf4j
@Service
public class MemberService extends AbstractService<Member, MemberRepository, MemberValidator> {
    public MemberService(MemberRepository repository, MemberValidator validator,
                         WorkspaceService workspaceService, CardService cardService, BoardService boardService) {
        super(repository, validator);
        this.workspaceService = workspaceService;
        this.cardService = cardService;
        this.boardService = boardService;
    }

    private final WorkspaceService workspaceService;
    private final CardService cardService;
    private final BoardService boardService;

    @Override
    public void delete(UUID id) {
        workspaceService.deleteMemberInWorkspaces(id);
        boardService.deleteMemberInBoards(id);
        cardService.deleteMemberInCards(id);
        super.delete(id);
    }

    public void deleteMembersForUser(UUID userId) {
        log.debug("Cascade delete members for user with id - {}", userId);
        repository.findByUserId(userId).forEach(member -> delete(member.getId()));
    }
}
