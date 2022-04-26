package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Card;
import spd.trello.domain.Color;
import spd.trello.domain.Label;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.LabelService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LabelTest {
    @Autowired
    private LabelService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        Card card = helper.getNewCard("create@LT.com");
        Color color = new Color();

        Label label = new Label();
        label.setCardId(card.getId());
        label.setColor(color);
        label.setName("name");
        Label testLabel = service.save(label);

        assertNotNull(testLabel);
        assertAll(
                () -> assertEquals(label.getName(), testLabel.getName()),
                () -> assertEquals(card.getId(), testLabel.getCardId()),
                () -> assertEquals(color, testLabel.getColor())
        );
    }

    @Test
    public void findAll() {
        Label firstLabel = helper.getNewLabel("findAll@LT.com");
        Label secondLabel = helper.getNewLabel("2findAll@LT.com");

        assertNotNull(firstLabel);
        assertNotNull(secondLabel);

        List<Label> testLabels = service.getAll();
        assertAll(
                () -> assertTrue(testLabels.contains(firstLabel)),
                () -> assertTrue(testLabels.contains(secondLabel))
        );
    }

    @Test
    public void findById() {
        Label label = helper.getNewLabel("findById@LT.com");

        Label testLabel = service.getById(label.getId());
        assertEquals(label, testLabel);
    }

    @Test
    public void delete() {
        Label label = helper.getNewLabel("delete@LT.com");

        assertNotNull(label);
        service.delete(label.getId());

        assertFalse(service.getAll().contains(label));
    }

    @Test
    public void update() {
        Label label = helper.getNewLabel("update@LT.com");
        Color color = label.getColor();
        color.setRed(100);
        color.setGreen(100);
        color.setBlue(100);

        label.setColor(color);
        label.setName("newName");

        Label testLabel = service.update(label);
        assertNotNull(testLabel);


        assertAll(
                () -> assertEquals(label.getName(), testLabel.getName()),
                () -> assertEquals(label.getCardId(), testLabel.getCardId()),
                () -> assertEquals(label.getColor().getRed(), testLabel.getColor().getRed()),
                () -> assertEquals(label.getColor().getGreen(), testLabel.getColor().getGreen()),
                () -> assertEquals(label.getColor().getBlue(), testLabel.getColor().getBlue())
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
        assertEquals("No class spd.trello.domain.Label entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void nullNameFieldCreate() {
        Card card = helper.getNewCard("nullNameFieldCreate@LT.com");
        Label label = new Label();
        label.setCardId(card.getId());
        label.setColor(helper.getNewColor());
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(label), "no exception"
        );
        assertEquals("Name cannot be null!", ex.getMessage());
    }

    @Test
    public void nullColorFieldCreate() {
        Card card = helper.getNewCard("nullColorFieldCreate@LT.com");
        Label label = new Label();
        label.setName("name");
        label.setCardId(card.getId());
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(label), "no exception"
        );
        assertEquals("Not found color!", ex.getMessage());
    }

    @Test
    public void nullCardIdCreate() {
        Label label = new Label();
        label.setName("name");
        label.setColor(helper.getNewColor());
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(label), "no exception"
        );
        assertEquals("The cardId field must belong to a card.", ex.getMessage());
    }

    @Test
    public void badNameFieldCreate() {
        Card card = helper.getNewCard("badNameFieldCreate@LT.com");
        Label label = new Label();
        label.setName("n");
        label.setCardId(card.getId());
        label.setColor(helper.getNewColor());
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(label), "no exception"
        );
        assertEquals("The name field must be between 2 and 20 characters long.", ex.getMessage());
    }

    @Test
    public void nullColorFieldsCreate() {
        Card card = helper.getNewCard("nullColorFieldsCreate@LT.com");
        Label label = new Label();
        label.setName("name");
        label.setCardId(card.getId());
        Color color = new Color();
        color.setRed(null);
        color.setGreen(null);
        color.setBlue(null);
        label.setColor(color);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(label), "no exception"
        );
        assertEquals("Fields red, green and blue must be filled!", ex.getMessage());
    }

    @Test
    public void badColorFieldsCreate() {
        Card card = helper.getNewCard("badColorFieldsCreate@LT.com");
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

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(label), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(redException)),
                () -> assertTrue(ex.getMessage().contains(greenException)),
                () -> assertTrue(ex.getMessage().contains(blueException))
        );
    }

    @Test
    public void nonExistentLabelUpdate() {
        Label label = helper.getNewLabel("nonExistentLU@LT.com");
        label.setId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(label), "no exception"
        );
        assertEquals("Cannot update non-existent label!", ex.getMessage());
    }

    @Test
    public void transferredCardUpdate() {
        Label label = helper.getNewLabel("transferredCardUpdate@LT.com");
        label.setCardId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(label), "no exception"
        );
        assertEquals("Label cannot be transferred to another card.", ex.getMessage());
    }

    @Test
    public void nullNameFieldUpdate() {
        Label label = helper.getNewLabel("nullNameFieldU@LT.com");
        label.setName(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(label), "no exception"
        );
        assertEquals("Name cannot be null!", ex.getMessage());
    }

    @Test
    public void nullColorUpdate() {
        Label label = helper.getNewLabel("nullColorUpdate@LT.com");
        label.setColor(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.update(label), "no exception"
        );
        assertEquals("Not found color!", ex.getMessage());
    }

    @Test
    public void badNameFieldUpdate() {
        Label label = helper.getNewLabel("badNameFieldLU@LT.com");
        label.setName("n");

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(label), "no exception"
        );
        assertEquals("The name field must be between 2 and 20 characters long.", ex.getMessage());
    }

    @Test
    public void nullColorFieldsUpdate() {
        Label label = helper.getNewLabel("nullColorFieldsUpdate@LT.com");
        Color color = new Color();
        color.setRed(null);
        color.setGreen(null);
        color.setBlue(null);
        label.setColor(color);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(label), "no exception"
        );
        assertEquals("Fields red, green and blue must be filled!", ex.getMessage());
    }

    @Test
    public void badColorFieldsUpdate() {
        Label label = helper.getNewLabel("badColorFields@LT.com");
        Color color = new Color();
        color.setRed(300);
        color.setGreen(300);
        color.setBlue(300);
        label.setColor(color);

        String redException = "The red color should be in the range 0 to 255. \n";
        String greenException = "The red color should be in the range 0 to 255. \n";
        String blueException = "The red color should be in the range 0 to 255. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(label), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(redException)),
                () -> assertTrue(ex.getMessage().contains(greenException)),
                () -> assertTrue(ex.getMessage().contains(blueException))
        );
    }
}
