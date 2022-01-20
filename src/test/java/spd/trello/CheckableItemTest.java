package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.CheckableItemRepository;
import spd.trello.services.CheckableItemService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static spd.trello.Helper.*;

public class CheckableItemTest extends BaseTest{
    public  CheckableItemTest() {
        service = new  CheckableItemService(new CheckableItemRepository(dataSource));
    }

    private final CheckableItemService service;

    @Test
    public void successCreate() {
        User user = getNewUser("successCreate@CIT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist checklist = getNewChecklist(member, card.getId());
        CheckableItem testCheckableItem = service.create(member, checklist.getId(), "testName");
        assertNotNull(testCheckableItem);
        assertAll(
                () -> assertEquals("testName", testCheckableItem.getName()),
                () -> assertFalse( testCheckableItem.getChecked()),
                () -> assertEquals(checklist.getId(), testCheckableItem.getChecklistId())
        );
    }

    @Test
    public void findAll() {
        User user = getNewUser("findAll@CIT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist checklist = getNewChecklist(member, card.getId());
        CheckableItem testFirstCheckableItem = service.create(member, checklist.getId(), "1CheckableItem");
        CheckableItem testSecondCheckableItem = service.create(member, checklist.getId(), "2CheckableItem");
        assertNotNull(testFirstCheckableItem);
        assertNotNull(testSecondCheckableItem);
        List<CheckableItem> testCheckableItems = service.findAll();
        assertAll(
                () -> assertTrue(testCheckableItems.contains(testFirstCheckableItem)),
                () -> assertTrue(testCheckableItems.contains(testSecondCheckableItem))
        );
    }

    @Test
    public void createFailure() {
        User user = getNewUser("createFailure@CIT");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "name"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("CheckableItem doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("CheckableItem with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = getNewUser("delete@CIT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist checklist = getNewChecklist(member, card.getId());
        CheckableItem testCheckableItem = service.create(member, checklist.getId(), "Checklist");
        assertNotNull(testCheckableItem);
        UUID id = testCheckableItem.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = getNewUser("update@CIT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Checklist checklist = getNewChecklist(member, card.getId());
        CheckableItem checkableItem = service.create(member, checklist.getId(), "CheckableItem");
        assertNotNull(checkableItem);
        checkableItem.setName("newName");
        checkableItem.setChecked(true);
        CheckableItem testCheckableItem = service.update(member, checkableItem);
        assertAll(
                () -> assertEquals("newName", testCheckableItem.getName()),
                () -> assertTrue(testCheckableItem.getChecked())
        );
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        CheckableItem testCheckableItem = new CheckableItem();
        testCheckableItem.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testCheckableItem),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("CheckableItem with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
