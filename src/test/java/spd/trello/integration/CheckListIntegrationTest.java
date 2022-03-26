package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Card;
import spd.trello.domain.Checklist;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CheckListIntegrationTest extends AbstractIntegrationTest<Checklist> {
    private final String URL_TEMPLATE = "/checklists";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Card card = helper.getNewCard("create@ChecklistIntegrationTest");
        Checklist checklist = new Checklist();
        checklist.setCreatedBy(card.getCreatedBy());
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
    public void createFailure() throws Exception {
        Checklist checklist = new Checklist();
        MvcResult mvcResult = super.create(URL_TEMPLATE, checklist);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        Checklist firstChecklist = helper.getNewChecklist("1findAll@ChecklistIntegrationTest");
        Checklist secondChecklist = helper.getNewChecklist("2findAll@ChecklistIntegrationTest");
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
        Checklist checklist = helper.getNewChecklist("findById@ChecklistIntegrationTest");
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
        Checklist checklist = helper.getNewChecklist("deleteById@ChecklistIntegrationTest");
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
        Checklist checklist = helper.getNewChecklist("update@ChecklistIntegrationTest");
        checklist.setName("newName");
        checklist.setUpdatedBy(checklist.getCreatedBy());
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
    public void updateFailure() throws Exception {
        Checklist checklist = helper.getNewChecklist("updateFailure@ChecklistIntegrationTest");
        checklist.setName(null);
        checklist.setCardId(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, checklist.getId(), checklist);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }
}
