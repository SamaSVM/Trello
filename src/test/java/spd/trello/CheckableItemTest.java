package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.services.CheckableItemService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CheckableItemTest {
    @Autowired
    private CheckableItemService service;

    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("successCreate@CIT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = helper.getNewChecklist(member, card.getId());
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("testName");
        CheckableItem testCheckableItem = service.create(checkableItem);
        assertNotNull(testCheckableItem);
        assertAll(
                () -> assertEquals("testName", testCheckableItem.getName()),
                () -> assertFalse(testCheckableItem.getChecked()),
                () -> assertEquals(checklist.getId(), testCheckableItem.getChecklistId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("findAll@CIT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = helper.getNewChecklist(member, card.getId());
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("1CheckableItem");
        CheckableItem testFirstCheckableItem = service.create(checkableItem);
        checkableItem.setName("2CheckableItem");
        CheckableItem testSecondCheckableItem = service.create(checkableItem);
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
        CheckableItem checkableItem = new CheckableItem();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(checkableItem),
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
        User user = helper.getNewUser("delete@CIT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = helper.getNewChecklist(member, card.getId());
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("Checklist");
        CheckableItem testCheckableItem = service.create(checkableItem);
        assertNotNull(testCheckableItem);
        UUID id = testCheckableItem.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@CIT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = helper.getNewChecklist(member, card.getId());
        CheckableItem updateCheckableItem = new CheckableItem();
        updateCheckableItem.setChecklistId(checklist.getId());
        updateCheckableItem.setName("CheckableItem");
        CheckableItem checkableItem = service.create(updateCheckableItem);
        assertNotNull(checkableItem);
        checkableItem.setName("newName");
        checkableItem.setChecked(true);
        CheckableItem testCheckableItem = service.update(checkableItem);
        assertAll(
                () -> assertEquals("newName", testCheckableItem.getName()),
                () -> assertTrue(testCheckableItem.getChecked())
        );
    }

    @Test
    public void updateFailure() {
        CheckableItem testCheckableItem = new CheckableItem();
        testCheckableItem.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testCheckableItem),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("CheckableItem with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void getAllCheckableItemsForChecklist() {
        User user = helper.getNewUser("getAllCheckableItemsForChecklist@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = helper.getNewChecklist(member, card.getId());
        CheckableItem firstCheckableItem = helper.getNewCheckableItem(checklist.getId());
        CheckableItem secondCheckableItem = helper.getNewCheckableItem(checklist.getId());
        assertNotNull(checklist);
        List<CheckableItem> checkableItems = service.getCheckableItems(checklist.getId());
        assertAll(
                () -> assertTrue(checkableItems.contains(firstCheckableItem)),
                () -> assertTrue(checkableItems.contains(secondCheckableItem)),
                () -> assertEquals(2, checkableItems.size())
        );
    }
}
