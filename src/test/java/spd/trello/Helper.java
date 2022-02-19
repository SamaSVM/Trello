package spd.trello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.services.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class Helper {
    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CardListService cardListService;
    @Autowired
    private CardService cardService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ChecklistService checklistService;
    @Autowired
    private LabelService labelService;


    public User getNewUser(String email) {
        User user = new User();
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setEmail(email);
        return userService.save(user);
    }

    public Member getNewMember(User user) {
        Member member = new Member();
        member.setUserId(user.getId());
        member.setCreatedBy(user.getEmail());
        member.setMemberRole(MemberRole.ADMIN);
        return memberService.save(member);
    }

    public Workspace getNewWorkspace(Member member) {
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("MemberName");
        workspace.setDescription("description");
        workspace.setCreatedBy(member.getId().toString());
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        workspace.setMembersIds(membersIds);
        return workspaceService.save(workspace);
    }

    public Board getNewBoard(Member member, UUID workspaceId) {
        Board board = new Board();
        board.setName("BoardName");
        board.setDescription("description");
        board.setWorkspaceId(workspaceId);
        board.setCreatedBy(member.getId().toString());
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        board.setMembersIds(membersIds);
        return boardService.save(board);
    }

    public CardList getNewCardList(Member member, UUID boardId) {
        CardList cardList = new CardList();
        cardList.setBoardId(boardId);
        cardList.setName("CardListName");
        cardList.setCreatedBy(member.getCreatedBy());
        return cardListService.save(cardList);
    }

    public Card getNewCard(Member member, UUID cardListId) {
        Card card = new Card();
        card.setName("CardName");
        card.setDescription("description");
        card.setCardListId(cardListId);
        card.setCreatedBy(member.getCreatedBy());
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        card.setMembersIds(membersIds);
        return cardService.save(card);
    }

    public Comment getNewComment(Member member, UUID cardId) {
        Comment comment = new Comment();
        comment.setText("testComment");
        comment.setCardId(cardId);
        comment.setCreatedBy(member.getId().toString());
        return commentService.save(comment);
    }

    public Checklist getNewChecklist(Member member, UUID cardId) {
        Checklist checklist = new Checklist();
        checklist.setName("testChecklist");
        checklist.setCardId(cardId);
        checklist.setCreatedBy(member.getCreatedBy());
        return checklistService.save(checklist);
    }

    public Label getNewLabel(UUID cardId) {
        Label label = new Label();
        label.setName("Label");
        label.setCardId(cardId);
        return labelService.save(label);
    }
}
