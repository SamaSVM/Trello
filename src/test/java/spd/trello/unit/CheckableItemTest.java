package spd.trello.unit;

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
    private UnitHelper helper;

    @Test
    public void create() {
        Checklist checklist = helper.getNewChecklist("create@CheckableItemTest");

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("testName");
        CheckableItem testCheckableItem = service.save(checkableItem);

        assertNotNull(testCheckableItem);
        assertAll(
                () -> assertEquals(checkableItem.getName(), testCheckableItem.getName()),
                () -> assertFalse(testCheckableItem.getChecked()),
                () -> assertEquals(checklist.getId(), testCheckableItem.getChecklistId())
        );
    }

    @Test
    public void findAll() {
        CheckableItem firstCheckableItem = helper.getNewCheckableItem("findAll@CheckableItemTest");
        CheckableItem secondCheckableItem = helper.getNewCheckableItem("2findAll@CheckableItemTest");

        assertNotNull(firstCheckableItem);
        assertNotNull(secondCheckableItem);

        List<CheckableItem> testCheckableItems = service.getAll();
        assertAll(
                () -> assertTrue(testCheckableItems.contains(firstCheckableItem)),
                () -> assertTrue(testCheckableItems.contains(secondCheckableItem))
        );
    }

    @Test
    public void findById() {
        Checklist checklist = helper.getNewChecklist("findById@CheckableItemTest");

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("testName");
        service.save(checkableItem);

        CheckableItem testCheckableItem = service.getById(checkableItem.getId());
        assertEquals(checkableItem, testCheckableItem);
    }

    @Test
    public void delete() {
        Checklist checklist = helper.getNewChecklist("delete@CheckableItemTest");

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
        CheckableItem checkableItem = helper.getNewCheckableItem("update@CheckableItemTest");

        assertNotNull(checkableItem);
        checkableItem.setName("newName");
        checkableItem.setChecked(true);
        CheckableItem testCheckableItem = service.update(checkableItem);

        assertAll(
                () -> assertEquals(checkableItem.getName(), testCheckableItem.getName()),
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
