package spd.trello.unit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class UnitHelper {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private CardListRepository cardListRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ChecklistRepository checklistRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private AttachmentRepository attachmentRepository;
    @Autowired
    private CheckableItemRepository checkableItemRepository;


    public User getNewUser(String email) {
        User user = new User();
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");
        user.setTimeZone("Europe/Kiev");
        user.setEmail(email);
        return userRepository.save(user);
    }

    public Member getNewMember(String email) {
        Member member = new Member();
        User user = getNewUser(email);
        member.setUserId(user.getId());
        member.setCreatedBy(user.getEmail());
        member.setCreatedDate(LocalDateTime.now().withNano(0));
        member.setMemberRole(MemberRole.ADMIN);
        return memberRepository.save(member);
    }

    public Workspace getNewWorkspace(String email) {
        Workspace workspace = new Workspace();
        Member member = getNewMember(email);
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setCreatedDate(LocalDateTime.now().withNano(0));
        workspace.setName("MemberName");
        workspace.setDescription("description");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        return workspaceRepository.save(workspace);
    }

    public Board getNewBoard(String email) {
        Workspace workspace = getNewWorkspace(email);
        Board board = new Board();
        board.setName("BoardName");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        Set<UUID> membersId = workspace.getMembersId();
        board.setMembersId(membersId);
        return boardRepository.save(board);
    }

    public CardList getNewCardList(String email) {
        Board board = getNewBoard(email);
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setName("CardListName");
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setCreatedDate(LocalDateTime.now().withNano(0));
        return cardListRepository.save(cardList);
    }

    public Card getNewCard(String email) {
        CardList cardList = getNewCardList(email);
        Reminder reminder = getNewReminder(email);
        Card card = new Card();
        card.setReminder(reminder);
        card.setName("CardName");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        Set<UUID> membersId = boardRepository.findById(cardList.getBoardId()).get().getMembersId();
        card.setMembersId(membersId);
        return cardRepository.save(card);
    }

    public Reminder getNewReminder(String email) {
        Reminder reminder = new Reminder();
        reminder.setCreatedBy(email);
        reminder.setCreatedDate(LocalDateTime.now().withNano(0));
        reminder.setRemindOn(LocalDateTime.now().plusHours(1));
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now().plusHours(2));
        return reminder;
    }

    public Comment getNewComment(String email) {
        Card card = getNewCard(email);
        Comment comment = new Comment();
        comment.setText("testComment");
        comment.setCardId(card.getId());
        comment.setCreatedBy(card.getCreatedBy());
        comment.setCreatedDate(LocalDateTime.now().withNano(0));
        return commentRepository.save(comment);
    }

    public Checklist getNewChecklist(String email) {
        Card card = getNewCard(email);
        Checklist checklist = new Checklist();
        checklist.setName("testChecklist");
        checklist.setCardId(card.getId());
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setCreatedDate(LocalDateTime.now().withNano(0));
        return checklistRepository.save(checklist);
    }

    public CheckableItem getNewCheckableItem(String email) {
        Checklist checklist = getNewChecklist(email);

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("Name");
        return checkableItemRepository.save(checkableItem);
    }

    public Label getNewLabel(String email) {
        Card card = getNewCard(email);
        Label label = new Label();
        label.setName("Label");
        label.setCardId(card.getId());
        label.setColor(getNewColor());
        return labelRepository.save(label);
    }

    public Color getNewColor() {
        Color color = new Color();
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        return color;
    }

    public Attachment getNewAttachment(String email) {
        Card card = getNewCard(email);
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setLink("http://www.example.com/product");
        attachment.setName("name");
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCreatedDate(LocalDateTime.now().withNano(0));
        return attachmentRepository.save(attachment);
    }
}
