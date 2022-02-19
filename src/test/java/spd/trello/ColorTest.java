package spd.trello;

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

    @Autowired
    private Helper helper;

    @Test
    public void create() {
        User user = helper.getNewUser("create@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = helper.getNewLabel(card.getId());

        Color color = new Color();
        color.setLabelId(label.getId());
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        Color testColor = service.save(color);

        assertNotNull(testColor);
        assertAll(
                () -> assertEquals(1, testColor.getRed()),
                () -> assertEquals(2, testColor.getGreen()),
                () -> assertEquals(3, testColor.getBlue()),
                () -> assertEquals(label.getId(), testColor.getLabelId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("findAll@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label firstLabel = helper.getNewLabel(card.getId());
        Label secondLabel = helper.getNewLabel(card.getId());

        Color firstColor = new Color();
        firstColor.setLabelId(firstLabel.getId());
        firstColor.setRed(1);
        firstColor.setGreen(2);
        firstColor.setBlue(3);
        Color testFirstColor = service.save(firstColor);

        Color secondColor = new Color();
        secondColor.setLabelId(secondLabel.getId());
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
        User user = helper.getNewUser("findById@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = helper.getNewLabel(card.getId());

        Color color = new Color();
        color.setLabelId(label.getId());
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        service.save(color);

        Color testColor = service.getById(color.getId());
        assertEquals(color, testColor);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = helper.getNewLabel(card.getId());

        Color color = new Color();
        color.setLabelId(label.getId());
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        Color testColor = service.save(color);

        assertNotNull(testColor);
        service.delete(testColor.getId());
        assertFalse(service.getAll().contains(testColor));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = helper.getNewLabel(card.getId());

        Color updateColor = new Color();
        updateColor.setLabelId(label.getId());
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
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Color()),
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
        assertEquals("No class spd.trello.domain.Color entity with id " + id + " exists!", ex.getMessage());
    }
}
