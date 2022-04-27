package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
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
        Checklist checklist = helper.getNewChecklist("create@ChecIT.com");

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
        CheckableItem firstCheckableItem = helper.getNewCheckableItem("findAll@ChecIT.com");
        CheckableItem secondCheckableItem = helper.getNewCheckableItem("2findAll@ChecIT.com");

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
        Checklist checklist = helper.getNewChecklist("findById@ChecIT.com");

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("testName");
        service.save(checkableItem);

        CheckableItem testCheckableItem = service.getById(checkableItem.getId());
        assertEquals(checkableItem, testCheckableItem);
    }

    @Test
    public void delete() {
        Checklist checklist = helper.getNewChecklist("delete@ChecIT.com");

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
        CheckableItem checkableItem = helper.getNewCheckableItem("update@ChecIT.com");

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
        assertEquals("No class spd.trello.domain.CheckableItem entity with id " + id + " exists!",
                ex.getMessage());
    }

    @Test
    public void nullCheckListCreate() {
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setName("name");
        checkableItem.setChecklistId(UUID.randomUUID());
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(checkableItem), "no exception"
        );
        assertEquals("The checklistId field must belong to a checklist.", ex.getMessage());
    }

    @Test
    public void nullNameFieldCreate() {
        Checklist checklist = helper.getNewChecklist("nullNameFieldCreate@ChecIT.com");
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(checkableItem), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void badNameFieldCreate() {
        Checklist checklist = helper.getNewChecklist("badNameFieldCreate@ChecIT.com");
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("n");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(checkableItem), "no exception"
        );
        assertEquals("The name field must be between 2 and 20 characters long. \n", ex.getMessage());
    }

    @Test
    public void nonExistentCheckableItemUpdate() {
        CheckableItem checkableItem = helper.getNewCheckableItem("nonExistCIU@ChecIT.com");
        checkableItem.setChecklistId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checkableItem), "no exception"
        );
        assertEquals("CheckableItem cannot be transferred to another checklist.", ex.getMessage());
    }

    @Test
    public void nullNameFieldsUpdate() {
        CheckableItem checkableItem = helper.getNewCheckableItem("nonExistentCIU@ChecIT.com");
        checkableItem.setName(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checkableItem), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void badNameFieldsUpdate() {
        CheckableItem checkableItem = helper.getNewCheckableItem("badNameFieldsU@ChecIT.com");
        checkableItem.setName("n");

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checkableItem), "no exception"
        );
        assertEquals("The name field must be between 2 and 20 characters long. \n", ex.getMessage());
    }
}
