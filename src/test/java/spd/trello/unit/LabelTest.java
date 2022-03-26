package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.LabelService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class LabelTest {
    @Autowired
    private LabelService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        Card card = helper.getNewCard("create@LT");
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
        Label firstLabel = helper.getNewLabel("findAll@LT");
        Label secondLabel = helper.getNewLabel("2findAll@LT");

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
        Label label = helper.getNewLabel("findById@LT");

        Label testLabel = service.getById(label.getId());
        assertEquals(label, testLabel);
    }

    @Test
    public void delete() {
        Label label = helper.getNewLabel("delete@LT");

        assertNotNull(label);
        service.delete(label.getId());

        assertFalse(service.getAll().contains(label));
    }

    @Test
    public void update() {
        Label label = helper.getNewLabel("update@LT");
        Color color = label.getColor();
        color.setId(null);
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
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Label()),
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
        assertEquals("No class spd.trello.domain.Label entity with id " + id + " exists!", ex.getMessage());
    }
}
