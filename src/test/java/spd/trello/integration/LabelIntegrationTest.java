package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Card;
import spd.trello.domain.Color;
import spd.trello.domain.Label;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LabelIntegrationTest extends AbstractIntegrationTest<Label>{
    private final String URL_TEMPLATE = "/labels";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Card card = helper.getNewCard("create@LIT.com");
        Color color = helper.getNewColor();
        Label label = new Label();
        label.setName("name");
        label.setCardId(card.getId());
        label.setColor(color);
        MvcResult mvcResult = super.create(URL_TEMPLATE, label);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(label.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(card.getId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertEquals(color.getId().toString(), getValue(mvcResult, "$.color.id"))
        );
    }

    @Test
    public void findAll() throws Exception {
        Label firstLabel = helper.getNewLabel("1findAll@LIT.com");
        Label secondLabel = helper.getNewLabel("2findAll@LIT.com");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Label> testLabels = helper.getLabelsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testLabels.contains(firstLabel)),
                () -> assertTrue(testLabels.contains(secondLabel))
        );
    }

    @Test
    public void findById() throws Exception {
        Label label = helper.getNewLabel("findById@LIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, label.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(label.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(label.getCardId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertEquals(label.getColor().getId().toString(), getValue(mvcResult, "$.color.id"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        Label label = helper.getNewLabel("deleteById@LIT.com");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, label.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Label> testLabels = helper.getLabelsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testLabels.contains(label))
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
        Label label = helper.getNewLabel("update@LIT.com");
        label.setName("newName");
        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(label.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(label.getCardId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertEquals(label.getColor().getId().toString(), getValue(mvcResult, "$.color.id"))
        );
    }

    @Test
    public void nullNameFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullNameFieldCreate@LIT.com");
        Label label = new Label();
        label.setCardId(card.getId());
        label.setColor(helper.getNewColor());

        MvcResult mvcResult = super.create(URL_TEMPLATE, label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Name cannot be null!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullColorFieldCreate() throws Exception{
        Card card = helper.getNewCard("nullColorFieldCreate@LIT.com");
        Label label = new Label();
        label.setName("name");
        label.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, label);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Not found color!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCardIdCreate() throws Exception{
        Label label = new Label();
        label.setName("name");
        label.setColor(helper.getNewColor());

        MvcResult mvcResult = super.create(URL_TEMPLATE, label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The cardId field must belong to a card.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badNameFieldCreate() throws Exception{
        Card card = helper.getNewCard("badNameFieldCreate@LIT.com");
        Label label = new Label();
        label.setName("n");
        label.setCardId(card.getId());
        label.setColor(helper.getNewColor());

        MvcResult mvcResult = super.create(URL_TEMPLATE, label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be between 2 and 20 characters long.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullColorFieldsCreate() throws Exception{
        Card card = helper.getNewCard("nullColorFieldsCreate@LIT.com");
        Label label = new Label();
        label.setName("name");
        label.setCardId(card.getId());
        Color color = new Color();
        color.setRed(null);
        color.setGreen(null);
        color.setBlue(null);
        label.setColor(color);

        MvcResult mvcResult = super.create(URL_TEMPLATE, label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Fields red, green and blue must be filled!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badColorFieldsCreate() throws Exception{
        Card card = helper.getNewCard("badColorFieldsCreate@LIT.com");
        Label label = new Label();
        label.setName("name");
        label.setCardId(card.getId());
        Color color = new Color();
        color.setRed(300);
        color.setGreen(300);
        color.setBlue(300);
        label.setColor(color);

        String redException = "The red color should be in the range 0 to 255. \n";
        String greenException = "The red color should be in the range 0 to 255. \n";
        String blueException = "The red color should be in the range 0 to 255. \n";

        MvcResult mvcResult = super.create(URL_TEMPLATE, label);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(redException)),
                () -> assertTrue(exceptionMessage.contains(greenException)),
                () -> assertTrue(exceptionMessage.contains(blueException))
        );
    }

    @Test
    public void nonExistentLabelUpdate() throws Exception{
        Label label = helper.getNewLabel("nonExistentLU@LIT.com");
        label.setId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent label!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void transferredCardUpdate() throws Exception{
        Label label = helper.getNewLabel("transferredCardUpdate@LIT.com");
        label.setCardId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Label cannot be transferred to another card.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameFieldUpdate() throws Exception{
        Label label = helper.getNewLabel("nullNameFieldU@LIT.com");
        label.setName(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Name cannot be null!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullColorUpdate()throws Exception {
        Label label = helper.getNewLabel("nullColorUpdate@LIT.com");
        label.setColor(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Not found color!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badNameFieldUpdate()throws Exception {
        Label label = helper.getNewLabel("badNameFieldLU@LIT.com");
        label.setName("n");

        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be between 2 and 20 characters long.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullColorFieldsUpdate()throws Exception {
        Label label = helper.getNewLabel("nullColorFieldsUpdate@LIT.com");
        Color color = new Color();
        color.setRed(null);
        color.setGreen(null);
        color.setBlue(null);
        label.setColor(color);
        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Fields red, green and blue must be filled!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badColorFieldsUpdate()throws Exception {
        Label label = helper.getNewLabel("badColorFields@LIT.com");
        Color color = new Color();
        color.setRed(300);
        color.setGreen(300);
        color.setBlue(300);
        label.setColor(color);

        String redException = "The red color should be in the range 0 to 255. \n";
        String greenException = "The red color should be in the range 0 to 255. \n";
        String blueException = "The red color should be in the range 0 to 255. \n";

        MvcResult mvcResult = super.update(URL_TEMPLATE, label.getId(), label);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(redException)),
                () -> assertTrue(exceptionMessage.contains(greenException)),
                () -> assertTrue(exceptionMessage.contains(blueException))
        );
    }
}
