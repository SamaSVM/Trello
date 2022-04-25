package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Card;
import spd.trello.domain.Checklist;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.ChecklistService;

import java.time.LocalDateTime;
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
        Card card = helper.getNewCard("successCreate@ChecklistT.com");

        Checklist checklist = new Checklist();
        checklist.setCardId(card.getId());
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setCreatedDate(LocalDateTime.now().withNano(0));
        checklist.setName("testName");
        Checklist testChecklist = service.save(checklist);

        assertNotNull(testChecklist);
        assertAll(
                () -> assertEquals(card.getCreatedBy(), testChecklist.getCreatedBy()),
                () -> assertNull(testChecklist.getUpdatedBy()),
                () -> assertEquals(checklist.getCreatedDate(), testChecklist.getCreatedDate()),
                () -> assertNull(testChecklist.getUpdatedDate()),
                () -> assertEquals(checklist.getName(), testChecklist.getName()),
                () -> assertEquals(card.getId(), testChecklist.getCardId())
        );
    }

    @Test
    public void findAll() {
        Checklist firstChecklist = helper.getNewChecklist("findAll@ChecklistT.com");
        Checklist secondChecklist = helper.getNewChecklist("2findAll@ChecklistT.com");

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
        Checklist checklist = helper.getNewChecklist("findById@ChecklistT.com");

        Checklist testChecklist = service.getById(checklist.getId());
        assertEquals(checklist, testChecklist);
    }

    @Test
    public void delete() {
        Checklist checklist = helper.getNewChecklist("delete@ChecklistT.com");

        assertNotNull(checklist);

        service.delete(checklist.getId());
        assertFalse(service.getAll().contains(checklist));
    }

    @Test
    public void update() {
        Checklist checklist = helper.getNewChecklist("update@ChecklistT.com");

        assertNotNull(checklist);

        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now().withNano(0));
        checklist.setName("newName");
        Checklist testChecklist = service.update(checklist);

        assertAll(
                () -> assertEquals(checklist.getCreatedBy(), testChecklist.getCreatedBy()),
                () -> assertEquals(checklist.getUpdatedBy(), testChecklist.getUpdatedBy()),
                () -> assertEquals(checklist.getCreatedDate(), testChecklist.getCreatedDate()),
                () -> assertEquals(checklist.getUpdatedDate(), testChecklist.getUpdatedDate()),
                () -> assertEquals(checklist.getName(), testChecklist.getName())
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
        assertEquals("No class spd.trello.domain.Checklist entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void validationCreate() {
        Checklist checklist = new Checklist();
        checklist.setCreatedBy("c");
        checklist.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        checklist.setName("n");
        checklist.setCardId(UUID.randomUUID());
        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String cardIdException = "The cardId field must belong to a card. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(checklist), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(cardIdException))
        );
    }

    @Test
    public void nullCreatedByFieldCreate() {
        Card card = helper.getNewCard("nullCreatedByFC@ChecklistT.com");
        Checklist checklist = new Checklist();
        checklist.setCreatedDate(LocalDateTime.now().withNano(0));
        checklist.setName("name");
        checklist.setCardId(card.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(checklist), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldCreate() {
        Card card = helper.getNewCard("nullCreatedDateFC@ChecklistT.com");
        Checklist checklist = new Checklist();
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setName("name");
        checklist.setCardId(card.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(checklist), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullTextFieldCreate() {
        Card card = helper.getNewCard("nullTextFC@ChecklistT.com");
        Checklist checklist = new Checklist();
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setCreatedDate(LocalDateTime.now());
        checklist.setCardId(card.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(checklist), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
        Checklist checklist = helper.getNewChecklist("validationUpdate@ChecklistT.com");
        checklist.setCreatedBy("c");
        checklist.setCreatedDate(LocalDateTime.now());
        checklist.setUpdatedBy("u");
        checklist.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        checklist.setName("n");
        checklist.setCardId(UUID.randomUUID());

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdByUpdateException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String updatedDateException = "The updatedDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String commentException = "CheckList cannot be transferred to another card. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checklist), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdByUpdateException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(updatedByException)),
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(commentException))
        );
    }

    @Test
    public void nonExistentCommentUpdate() {
        Checklist checklist = helper.getNewChecklist("nonExistentCardLU@ChecklistT.com");
        checklist.setId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checklist), "no exception"
        );
        assertEquals("Cannot update non-existent checklist!", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldsUpdate() {
        Checklist checklist = helper.getNewChecklist("nullCreatedByFU@ChecklistT.com");
        checklist.setCreatedBy(null);
        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checklist), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldsUpdate() {
        Checklist checklist = helper.getNewChecklist("nullCreatedDFU@ChecklistT.com");
        checklist.setCreatedDate(null);
        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checklist), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedByFieldsUpdate() {
        Checklist checklist = helper.getNewChecklist("nullUpdatedByFieldsUpdate@ChecklistT.com");
        checklist.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checklist), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() {
        Checklist checklist = helper.getNewChecklist("nullUpdatedDateFU@ChecklistT.com");
        checklist.setUpdatedBy(checklist.getCreatedBy());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checklist), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }

    @Test
    public void nullNameUpdate() {
        Checklist checklist = helper.getNewChecklist("nullNameUpdate@ChecklistT.com");
        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now());
        checklist.setName(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(checklist), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }
}
