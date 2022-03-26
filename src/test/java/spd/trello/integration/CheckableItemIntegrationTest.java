package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.CheckableItem;
import spd.trello.domain.Checklist;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CheckableItemIntegrationTest extends AbstractIntegrationTest<CheckableItem> {
    private final String URL_TEMPLATE = "/checkableitems";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Checklist checklist = helper.getNewChecklist("create@CheckableItemIntegrationTest");

        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setChecklistId(checklist.getId());
        checkableItem.setName("name");

        MvcResult mvcResult = super.create(URL_TEMPLATE, checkableItem);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(checkableItem.getName(), getValue(mvcResult, "$.name")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.checked")),
                () -> assertEquals(checklist.getId().toString(), getValue(mvcResult, "$.checklistId"))
        );
    }

    @Test
    public void createFailure() throws Exception {
        CheckableItem checkableItem = new CheckableItem();
        MvcResult mvcResult = super.create(URL_TEMPLATE, checkableItem);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        CheckableItem firstCheckableItem = helper.getNewCheckableItem("1findAll@CheckableItemIntegrationTest");
        CheckableItem secondCheckableItem = helper.getNewCheckableItem("2findAll@CheckableItemIntegrationTest");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<CheckableItem> testLabels = helper.getCheckableItemsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testLabels.contains(firstCheckableItem)),
                () -> assertTrue(testLabels.contains(secondCheckableItem))
        );
    }

    @Test
    public void findById() throws Exception {
        CheckableItem checkableItem = helper.getNewCheckableItem("findById@CheckableItemIntegrationTest");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, checkableItem.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(checkableItem.getName(), getValue(mvcResult, "$.name")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.checked")),
                () -> assertEquals
                        (checkableItem.getChecklistId().toString(), getValue(mvcResult, "$.checklistId"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        CheckableItem checkableItem = helper.getNewCheckableItem("deleteById@CheckableItemIntegrationTest");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, checkableItem.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<CheckableItem> testCheckableItems = helper.getCheckableItemsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testCheckableItems.contains(checkableItem))
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
        CheckableItem checkableItem = helper.getNewCheckableItem("update@CheckableItemIntegrationTest");
        checkableItem.setName("newName");
        checkableItem.setChecked(true);
        MvcResult mvcResult = super.update(URL_TEMPLATE, checkableItem.getId(), checkableItem);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(checkableItem.getName(), getValue(mvcResult, "$.name")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.checked")),
                () -> assertEquals
                        (checkableItem.getChecklistId().toString(), getValue(mvcResult, "$.checklistId"))
        );
    }

    @Test
    public void updateFailure() throws Exception {
        CheckableItem checkableItem = helper.getNewCheckableItem("updateFailure@CheckableItemIntegrationTest");
        checkableItem.setName(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, checkableItem.getId(), checkableItem);

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }
}

