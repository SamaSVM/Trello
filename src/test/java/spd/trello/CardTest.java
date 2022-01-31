package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.services.CardService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CardTest {
    @Autowired
    private CardService service;

    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test19@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
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
        User user = helper.getNewUser("test20@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
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
        User user = helper.getNewUser("test21@mail");
        Member member = helper.getNewMember(user);
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
        User user = helper.getNewUser("test22@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card testCard = service.create(member, cardList.getId(), "testCard", "description");
        assertNotNull(testCard);
        UUID id = testCard.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void testUpdate() {
        User user = helper.getNewUser("test23@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
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
        User firstUser = helper.getNewUser("addAndDeleteSecondMember1@CT");
        User secondUser = helper.getNewUser("addAndDeleteSecondMember2@CT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board board = helper.getNewBoard(firstMember, workspace.getId());
        CardList cardList = helper.getNewCardList(firstMember, board.getId());
        Card testCard = service.create(firstMember, cardList.getId(), "testCard", "testDescription");
        assertNotNull(testCard);
        assertAll(
                () -> assertTrue(service.addMember(firstMember, secondMember.getId(), testCard.getId())),
                () -> assertTrue(service.deleteMember(firstMember, secondMember.getId(), testCard.getId()))
        );
    }

    @Test
    public void getAllMembersForCard() {
        User firstUser = helper.getNewUser("getAllMembersForCard1@CT");
        User secondUser = helper.getNewUser("getAllMembersForCard2@CT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board board = helper.getNewBoard(firstMember, workspace.getId());
        CardList cardList = helper.getNewCardList(firstMember, board.getId());
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
        User user = helper.getNewUser("getAllCommentsForCard@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "testCard", "testDescription");
        Comment firstComment = helper.getNewComment(member, card.getId());
        Comment secondComment = helper.getNewComment(member, card.getId());
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
        User user = helper.getNewUser("getAllRemindersForCard@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "testCard", "testDescription");
        Reminder firstReminder = helper.getNewReminder(member, card.getId());
        Reminder secondReminder = helper.getNewReminder(member, card.getId());
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
        User user = helper.getNewUser("getAllChecklistsForCard@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "testCard", "testDescription");
        Checklist firstChecklist = helper.getNewChecklist(member, card.getId());
        Checklist secondChecklist = helper.getNewChecklist(member, card.getId());
        assertNotNull(card);
        List<Checklist> comments = service.getAllChecklists(member, card.getId());
        assertAll(
                () -> assertTrue(comments.contains(firstChecklist)),
                () -> assertTrue(comments.contains(secondChecklist)),
                () -> assertEquals(2, comments.size())
        );
    }

    @Test
    public void getAllLabelsForCard() {
        User user = helper.getNewUser("getAllLabelsForCard@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = service.create(member, cardList.getId(), "testCard", "testDescription");
        Label firstLabel = helper.getNewLabel(member, card.getId());
        Label secondLabel = helper.getNewLabel(member, card.getId());
        assertNotNull(card);
        List<Label> labels = service.getAllLabels(member, card.getId());
        assertAll(
                () -> assertTrue(labels.contains(firstLabel)),
                () -> assertTrue(labels.contains(secondLabel)),
                () -> assertEquals(2, labels.size())
        );
    }
}
