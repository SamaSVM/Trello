package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.ColorService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ColorTest {
    @Autowired
    private ColorService service;

    @Test
    public void create() {
        Color color = new Color();
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        Color testColor = service.save(color);

        assertNotNull(testColor);
        assertAll(
                () -> assertEquals(1, testColor.getRed()),
                () -> assertEquals(2, testColor.getGreen()),
                () -> assertEquals(3, testColor.getBlue())
        );
    }

    @Test
    public void findAll() {
        Color firstColor = new Color();
        firstColor.setRed(1);
        firstColor.setGreen(2);
        firstColor.setBlue(3);
        Color testFirstColor = service.save(firstColor);

        Color secondColor = new Color();
        secondColor.setRed(4);
        secondColor.setGreen(5);
        secondColor.setBlue(6);
        Color testSecondColor = service.save(secondColor);

        assertNotNull(testFirstColor);
        assertNotNull(testSecondColor);
        List<Color> testColors = service.getAll();
        assertAll(
                () -> assertTrue(testColors.contains(testFirstColor)),
                () -> assertTrue(testColors.contains(testSecondColor))
        );
    }

    @Test
    public void findById() {
        Color color = new Color();
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        service.save(color);

        Color testColor = service.getById(color.getId());
        assertEquals(color, testColor);
    }

    @Test
    public void delete() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.delete(new Color().getId()),
                "no exception"
        );
        assertTrue(ex.getMessage().contains("This method cannot be called!"));
    }

    @Test
    public void update() {
        Color updateColor = new Color();
        updateColor.setRed(1);
        updateColor.setGreen(2);
        updateColor.setBlue(3);
        Color color = service.save(updateColor);

        assertNotNull(color);
        color.setRed(4);
        color.setGreen(5);
        color.setBlue(6);
        Color testColor = service.update(color);

        assertAll(
                () -> assertEquals(4, testColor.getRed()),
                () -> assertEquals(5, testColor.getGreen()),
                () -> assertEquals(6, testColor.getBlue())
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
}
