package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.CardListRepository;
import spd.trello.repository.CardRepository;
import spd.trello.services.CardListService;
import spd.trello.services.CardService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static spd.trello.Helper.*;
import static spd.trello.Helper.getNewBoard;

public class CardTest extends BaseTest{
    public CardTest() {
        service = new CardService(new CardRepository(dataSource));
    }

    private final CardService service;

    @Test
    public void successCreate() {
        User user = getNewUser("test19@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card testCard = service.create(member, cardList.getId(), "testCardName", "description");
        assertNotNull(testCard);
        assertAll(
                () -> assertEquals("test19@mail", testCard.getCreatedBy()),
                () -> assertNull(testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertNull(testCard.getUpdatedDate()),
                () -> assertEquals("testCardName", testCard.getName()),
                () -> assertEquals("description", testCard.getDescription()),
                () -> assertFalse(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId())
        );
    }

    @Test
    public void testFindAll() {
        User user = getNewUser("test20@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card testFirstCard = service.create(member, cardList.getId(), "1Card", "description");
        Card testSecondCard = service.create(member, cardList.getId(), "2Card", "description");
        assertNotNull(testFirstCard);
        assertNotNull(testSecondCard);
        List<Card> testCard = service.findAll();
        assertAll(
                () -> assertTrue(testCard.contains(testFirstCard)),
                () -> assertTrue(testCard.contains(testSecondCard))
        );
    }

    @Test
    public void createFailure() {
        User user = getNewUser("test21@mail");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Name", "description"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Card doesn't creates", ex.getMessage());
    }

    @Test
    public void testFindById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Card with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void testDelete() {
        User user = getNewUser("test22@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card testCard = service.create(member,cardList.getId(), "testCard", "description");
        assertNotNull(testCard);
        UUID id = testCard.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void testUpdate() {
        User user = getNewUser("test23@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "name", "description");
        assertNotNull(card);
        card.setName("newCard");
        card.setDescription("newDescription");
        card.setArchived(true);
        Card testCard = service.update(member, card);
        assertAll(
                () -> assertEquals("test23@mail", testCard.getCreatedBy()),
                () -> assertEquals("test23@mail", testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getUpdatedDate()),
                () -> assertEquals("newCard", testCard.getName()),
                () -> assertEquals("newDescription", testCard.getDescription()),
                () -> assertTrue(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId())
        );
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Card testCard = new Card();
        testCard.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testCard),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("This member cannot update card!", ex.getMessage());
    }

    @Test
    public void addAndDeleteSecondMember() {
        User firstUser = getNewUser("addAndDeleteSecondMember1@CT");
        User secondUser = getNewUser("addAndDeleteSecondMember2@CT");
        Member firstMember = getNewMember(firstUser);
        Member secondMember = getNewMember(secondUser);
        Workspace workspace = getNewWorkspace(firstMember);
        Board board = getNewBoard(firstMember, workspace.getId());
        CardList cardList = getNewCardList(firstMember, board.getId());
        Card testCard = service.create(firstMember, cardList.getId(), "testCard", "testDescription");
        assertNotNull(testCard);
        assertAll(
                () -> assertTrue(service.addMember(firstMember, secondMember.getId(), testCard.getId())),
                () -> assertTrue(service.deleteMember(firstMember, secondMember.getId(), testCard.getId()))
        );
    }

    @Test
    public void getAllMembersForCard() {
        User firstUser = getNewUser("getAllMembersForCard1@CT");
        User secondUser = getNewUser("getAllMembersForCard2@CT");
        Member firstMember = getNewMember(firstUser);
        Member secondMember = getNewMember(secondUser);
        Workspace workspace = getNewWorkspace(firstMember);
        Board board = getNewBoard(firstMember, workspace.getId());
        CardList cardList = getNewCardList(firstMember, board.getId());
        Card testCard = service.create(firstMember, cardList.getId(), "testCard", "testDescription");
        service.addMember(firstMember, secondMember.getId(), testCard.getId());
        assertNotNull(testCard);
        List<Member> members = service.getAllMembers(firstMember, testCard.getId());
        assertAll(
                () -> assertTrue(members.contains(firstMember)),
                () -> assertTrue(members.contains(secondMember)),
                () -> assertEquals(2, members.size())
        );
    }

    @Test
    public void getAllCommentsForCard() {
        User user = getNewUser("getAllCommentsForCard@CT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "testCard", "testDescription");
        Comment firstComment = getNewComment(member, card.getId());
        Comment secondComment = getNewComment(member, card.getId());
        assertNotNull(card);
        List<Comment> comments = service.getAllComments(member, card.getId());
        assertAll(
                () -> assertTrue(comments.contains(firstComment)),
                () -> assertTrue(comments.contains(secondComment)),
                () -> assertEquals(2, comments.size())
        );
    }

    @Test
    public void getAllRemindersForCard() {
        User user = getNewUser("getAllRemindersForCard@CT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "testCard", "testDescription");
        Reminder firstReminder = getNewReminder(member, card.getId());
        Reminder secondReminder = getNewReminder(member, card.getId());
        assertNotNull(card);
        List<Reminder> comments = service.getAllReminders(member, card.getId());
        assertAll(
                () -> assertTrue(comments.contains(firstReminder)),
                () -> assertTrue(comments.contains(secondReminder)),
                () -> assertEquals(2, comments.size())
        );
    }

    @Test
    public void getAllChecklistsForCard() {
        User user = getNewUser("getAllChecklistsForCard@CT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "testCard", "testDescription");
        Checklist firstChecklist = getNewChecklist(member, card.getId());
        Checklist secondChecklist = getNewChecklist(member, card.getId());
        assertNotNull(card);
        List<Checklist> comments = service.getAllChecklists(member, card.getId());
        assertAll(
                () -> assertTrue(comments.contains(firstChecklist)),
                () -> assertTrue(comments.contains(secondChecklist)),
                () -> assertEquals(2, comments.size())
        );
    }
}
