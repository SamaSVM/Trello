package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.ChecklistRepository;
import spd.trello.services.ChecklistService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static spd.trello.Helper.*;

public class ChecklistTest extends BaseTest{
    private final ChecklistService service = context.getBean(ChecklistService.class);

    @Test
    public void successCreate() {
        User user = getNewUser("successCreate@CLT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist testChecklist = service.create(member, card.getId(), "testName");
        assertNotNull(testChecklist);
        assertAll(
                () -> assertEquals("successCreate@CLT", testChecklist.getCreatedBy()),
                () -> assertNull(testChecklist.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getCreatedDate()),
                () -> assertNull(testChecklist.getUpdatedDate()),
                () -> assertEquals("testName", testChecklist.getName()),
                () -> assertEquals(card.getId(), testChecklist.getCardId())
        );
    }

    @Test
    public void findAll() {
        User user = getNewUser("findAll@CLT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist testFirstChecklist = service.create(member, card.getId(), "1 Checklist");
        Checklist testSecondChecklist = service.create(member, card.getId(), "2 Checklist");
        assertNotNull(testFirstChecklist);
        assertNotNull(testSecondChecklist);
        List<Checklist> testComments = service.findAll();
        assertAll(
                () -> assertTrue(testComments.contains(testFirstChecklist)),
                () -> assertTrue(testComments.contains(testSecondChecklist))
        );
    }

    @Test
    public void createFailure() {
        User user = getNewUser("createFailure@CLT");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "name"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Checklist doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Checklist with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = getNewUser("delete@CLT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist testChecklist = service.create(member, card.getId(), "Checklist");
        assertNotNull(testChecklist);
        UUID id = testChecklist.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = getNewUser("update@CLT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist checklist = service.create(member, card.getId(), "Checklist");
        assertNotNull(checklist);
        checklist.setName("newName");
        Checklist testChecklist = service.update(member, checklist);
        assertAll(
                () -> assertEquals("update@CLT", testChecklist.getCreatedBy()),
                () -> assertEquals("update@CLT", testChecklist.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getUpdatedDate()),
                () -> assertEquals("newName", testChecklist.getName())
        );
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Checklist testChecklist = new Checklist();
        testChecklist.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testChecklist),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Checklist with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void getAllCheckableItemsForChecklist() {
        User user = getNewUser("getAllCheckableItemsForChecklist@CLT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist checklist = service.create(member, card.getId(), "Checklist");
        CheckableItem firstCheckableItem = getNewCheckableItem(member, checklist.getId());
        CheckableItem secondCheckableItem = getNewCheckableItem(member, checklist.getId());
        assertNotNull(checklist);
        List<CheckableItem> comments = service.getCheckableItems(member, checklist.getId());
        assertAll(
                () -> assertTrue(comments.contains(firstCheckableItem)),
                () -> assertTrue(comments.contains(secondCheckableItem)),
                () -> assertEquals(2, comments.size())
        );
    }
}
