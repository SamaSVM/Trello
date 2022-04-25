package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Card;
import spd.trello.domain.Checklist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CheckListIntegrationTest extends AbstractIntegrationTest<Checklist> {
    private final String URL_TEMPLATE = "/checklists";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Card card = helper.getNewCard("create@ChecklistIT.com");
        Checklist checklist = new Checklist();
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setCreatedDate(LocalDateTime.now().withNano(0));
        checklist.setName("name");
        checklist.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, checklist);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(checklist.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(checklist.getCardId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertNotNull(getValue(mvcResult, "$.checkableItems"))
        );
    }

    @Test
    public void findAll() throws Exception {
        Checklist firstChecklist = helper.getNewChecklist("1findAll@ChecklistIT.com");
        Checklist secondChecklist = helper.getNewChecklist("2findAll@ChecklistIT.com");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Checklist> testLabels = helper.getChecklistsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testLabels.contains(firstChecklist)),
                () -> assertTrue(testLabels.contains(secondChecklist))
        );
    }

    @Test
    public void findById() throws Exception {
        Checklist checklist = helper.getNewChecklist("findById@ChecklistIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, checklist.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(checklist.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(checklist.getCardId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertNotNull(getValue(mvcResult, "$.checkableItems"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        Checklist checklist = helper.getNewChecklist("deleteById@ChecklistIT.com");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, checklist.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Checklist> testChecklists = helper.getChecklistsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testChecklists.contains(checklist))
        );
    }

    @Test
    public void deleteByIdFailure() throws Exception {
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, UUID.randomUUID());

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus())
        );
    }

    @Test
    public void update() throws Exception {
        Checklist checklist = helper.getNewChecklist("update@ChecklistIT.com");
        checklist.setName("newName");
        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now().withNano(0));
        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(checklist.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(checklist.getCardId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertNotNull(getValue(mvcResult, "$.checkableItems"))
        );
    }

    @Test
    public void validationCreate() throws Exception {
        Checklist checklist = new Checklist();
        checklist.setCreatedBy("c");
        checklist.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        checklist.setName("n");
        checklist.setCardId(UUID.randomUUID());
        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String cardIdException = "The cardId field must belong to a card. \n";

        MvcResult mvcResult = super.create(URL_TEMPLATE, checklist);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(cardIdException))
        );
    }

    @Test
    public void nullCreatedByFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullCreatedByFC@ChecklistIT.com");
        Checklist checklist = new Checklist();
        checklist.setCreatedDate(LocalDateTime.now().withNano(0));
        checklist.setName("name");
        checklist.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullCreatedDateFC@ChecklistIT.com");
        Checklist checklist = new Checklist();
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setName("name");
        checklist.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullTextFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullTextFC@ChecklistIT.com");
        Checklist checklist = new Checklist();
        checklist.setCreatedBy(card.getCreatedBy());
        checklist.setCreatedDate(LocalDateTime.now());
        checklist.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void validationUpdate() throws Exception {
        Checklist checklist = helper.getNewChecklist("validationUpdate@ChecklistIT.com");
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

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdByUpdateException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(updatedByException)),
                () -> assertTrue(exceptionMessage.contains(updatedDateException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(commentException))
        );
    }

    @Test
    public void nonExistentCommentUpdate() throws Exception {
        Checklist checklist = helper.getNewChecklist("nonExistentCardLU@ChecklistIT.com");
        checklist.setId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent checklist!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldsUpdate() throws Exception {
        Checklist checklist = helper.getNewChecklist("nullCreatedByFU@ChecklistIT.com");
        checklist.setCreatedBy(null);
        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldsUpdate() throws Exception {
        Checklist checklist = helper.getNewChecklist("nullCreatedDFU@ChecklistIT.com");
        checklist.setCreatedDate(null);
        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByFieldsUpdate() throws Exception {
        Checklist checklist = helper.getNewChecklist("nullUpdatedByFieldsUpdate@ChecklistIT.com");
        checklist.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() throws Exception {
        Checklist checklist = helper.getNewChecklist("nullUpdatedDateFU@ChecklistIT.com");
        checklist.setUpdatedBy(checklist.getCreatedBy());

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameUpdate() throws Exception {
        Checklist checklist = helper.getNewChecklist("nullNameUpdate@ChecklistIT.com");
        checklist.setUpdatedBy(checklist.getCreatedBy());
        checklist.setUpdatedDate(LocalDateTime.now());
        checklist.setName(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }
}
