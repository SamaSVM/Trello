package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
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
    public void create() {
        User user = helper.getNewUser("create@CIT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = helper.getNewChecklist(member, card.getId());

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("testName");
        CheckableItem testCheckableItem = service.save(checkableItem);

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

        CheckableItem firstCheckableItem = new CheckableItem();
        firstCheckableItem.setChecklistId(checklist.getId());
        firstCheckableItem.setName("1CheckableItem");
        CheckableItem testFirstCheckableItem = service.save(firstCheckableItem);

        CheckableItem secondCheckableItem = new CheckableItem();
        secondCheckableItem.setChecklistId(checklist.getId());
        secondCheckableItem.setName("2CheckableItem");
        CheckableItem testSecondCheckableItem = service.save(secondCheckableItem);

        assertNotNull(testFirstCheckableItem);
        assertNotNull(testSecondCheckableItem);
        List<CheckableItem> testCheckableItems = service.getAll();
        assertAll(
                () -> assertTrue(testCheckableItems.contains(testFirstCheckableItem)),
                () -> assertTrue(testCheckableItems.contains(testSecondCheckableItem))
        );
    }

    @Test
    public void findById() {
        User user = helper.getNewUser("findById@CIT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = helper.getNewChecklist(member, card.getId());

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("testName");
        service.save(checkableItem);

        CheckableItem testCheckableItem = service.getById(checkableItem.getId());
        assertEquals(checkableItem, testCheckableItem);
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
        CheckableItem testCheckableItem = service.save(checkableItem);

        assertNotNull(testCheckableItem);
        service.delete(testCheckableItem.getId());
        assertFalse(service.getAll().contains(testCheckableItem));
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
        CheckableItem checkableItem = service.save(updateCheckableItem);

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
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new CheckableItem()),
                "no exception"
        );
        assertTrue(ex.getMessage().contains("could not execute statement;"));
    }

    @Test
    public void findByIdFailure() {
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getById(UUID.randomUUID()),
                "no exception"
        );
        assertEquals("Resource not found Exception!", ex.getMessage());
    }

    @Test
    public void deleteFailure() {
        UUID id = UUID.randomUUID();
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.delete(id),
                "no exception"
        );
        assertEquals("No class spd.trello.domain.CheckableItem entity with id " + id + " exists!", ex.getMessage());
    }
}
