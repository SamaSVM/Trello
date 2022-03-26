package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.ChecklistService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChecklistTest {
    @Autowired
    private ChecklistService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        Card card = helper.getNewCard("successCreate@CLT");

        Checklist checklist = new Checklist();
        checklist.setCardId(card.getId());
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setName("testName");
        Checklist testChecklist = service.save(checklist);

        assertNotNull(testChecklist);
        assertAll(
                () -> assertEquals(card.getCreatedBy(), testChecklist.getCreatedBy()),
                () -> assertNull(testChecklist.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getCreatedDate()),
                () -> assertNull(testChecklist.getUpdatedDate()),
                () -> assertEquals(checklist.getName(), testChecklist.getName()),
                () -> assertEquals(card.getId(), testChecklist.getCardId())
        );
    }

    @Test
    public void findAll() {
        Checklist firstChecklist = helper.getNewChecklist("findAll@ChecklistT");
        Checklist secondChecklist = helper.getNewChecklist("2findAll@ChecklistT");

        assertNotNull(firstChecklist);
        assertNotNull(secondChecklist);
        List<Checklist> testChecklists = service.getAll();
        assertAll(
                () -> assertTrue(testChecklists.contains(firstChecklist)),
                () -> assertTrue(testChecklists.contains(secondChecklist))
        );
    }

    @Test
    public void findById() {
        Checklist checklist = helper.getNewChecklist("findById@ChecklistT");

        Checklist testChecklist = service.getById(checklist.getId());
        assertEquals(checklist, testChecklist);
    }

    @Test
    public void delete() {
        Checklist checklist = helper.getNewChecklist("delete@ChecklistT");

        assertNotNull(checklist);

        service.delete(checklist.getId());
        assertFalse(service.getAll().contains(checklist));
    }

    @Test
    public void update() {
        Checklist checklist = helper.getNewChecklist("update@ChecklistT");

        assertNotNull(checklist);

        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setName("newName");
        Checklist testChecklist = service.update(checklist);

        assertAll(
                () -> assertEquals(checklist.getCreatedBy(), testChecklist.getCreatedBy()),
                () -> assertEquals(checklist.getUpdatedBy(), testChecklist.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getUpdatedDate()),
                () -> assertEquals(checklist.getName(), testChecklist.getName())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Checklist()),
                "no exception"
        );
        assertTrue(ex.getMessage().contains("not-null property references a null or transient value"));
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
        assertEquals("No class spd.trello.domain.Checklist entity with id " + id + " exists!", ex.getMessage());
    }
}
