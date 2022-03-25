package spd.trello.unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.services.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class UnitHelper {
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

    public Member getNewMember(String email) {
        Member member = new Member();
        User user = getNewUser(email);
        member.setUserId(user.getId());
        member.setCreatedBy(user.getEmail());
        member.setMemberRole(MemberRole.ADMIN);
        return memberService.save(member);
    }

    public Workspace getNewWorkspace(String email) {
        Workspace workspace = new Workspace();
        Member member = getNewMember(email);
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("MemberName");
        workspace.setDescription("description");
        workspace.setCreatedBy(member.getId().toString());
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        return workspaceService.save(workspace);
    }

    public Board getNewBoard(String email) {
        Workspace workspace = getNewWorkspace(email);
        Board board = new Board();
        board.setName("BoardName");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setCreatedBy(workspace.getCreatedBy());
        Set<UUID> membersId = workspace.getMembersId();
        board.setMembersId(membersId);
        return boardService.save(board);
    }

    public CardList getNewCardList(String email) {
        Board board = getNewBoard(email);
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setName("CardListName");
        cardList.setCreatedBy(board.getCreatedBy());
        return cardListService.save(cardList);
    }

    public Card getNewCard(String email) {
        CardList cardList = getNewCardList(email);
        Card card = new Card();
        card.setName("CardName");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);
        return cardService.save(card);
    }

    public Comment getNewComment(String email) {
        Card card = getNewCard(email);
        Comment comment = new Comment();
        comment.setText("testComment");
        comment.setCardId(card.getId());
        comment.setCreatedBy(card.getCreatedBy());
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

    public Color getNewColor(){
        Color color = new Color();
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        return color;
    }
}
