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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LabelIntegrationTest extends AbstractIntegrationTest<Label>{
    private final String URL_TEMPLATE = "/labels";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Card card = helper.getNewCard("create@LabelIntegrationTest");
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
    public void createFailure() throws Exception {
        Label label = new Label();
        MvcResult mvcResult = super.create(URL_TEMPLATE, label);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        Label firstLabel = helper.getNewLabel("1findAll@LabelIntegrationTest");
        Label secondLabel = helper.getNewLabel("2findAll@LabelIntegrationTest");
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
        Label label = helper.getNewLabel("findById@LabelIntegrationTest");
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
        Label label = helper.getNewLabel("deleteById@LabelIntegrationTest");
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
        Label label = helper.getNewLabel("update@LabelIntegrationTest");
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
    public void updateFailure() throws Exception {
        Label firstLabel = helper.getNewLabel("updateFailure@LabelIntegrationTest");
        firstLabel.setName(null);
        firstLabel.setCardId(null);
        firstLabel.setColor(null);

        Label secondLabel = new Label();
        secondLabel.setId(firstLabel.getId());

        MvcResult firstMvcResult = super.update(URL_TEMPLATE, firstLabel.getId(), firstLabel);
        MvcResult secondMvcResult = super.update(URL_TEMPLATE, secondLabel.getId(), secondLabel);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), secondMvcResult.getResponse().getStatus())
        );
    }
}
