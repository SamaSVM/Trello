package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.services.ColorService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ColorTest {
    @Autowired
    private ColorService service;

    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("successCreate@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = helper.getNewLabel(member, card.getId());
        Color color = new Color();
        color.setLabelId(label.getId());
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        Color testColor = service.create(color);
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
        Label firstLabel = helper.getNewLabel(member, card.getId());
        Label secondLabel = helper.getNewLabel(member, card.getId());
        Color color = new Color();
        color.setLabelId(firstLabel.getId());
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        Color testFirstColor = service.create(color);
        color.setLabelId(secondLabel.getId());
        color.setRed(4);
        color.setGreen(5);
        color.setBlue(6);
        Color testSecondColor = service.create(color);
        assertNotNull(testFirstColor);
        assertNotNull(testSecondColor);
        List<Color> testColors = service.findAll();
        assertAll(
                () -> assertTrue(testColors.contains(testFirstColor)),
                () -> assertTrue(testColors.contains(testSecondColor))
        );
    }

    @Test
    public void findByIdFailure() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Color with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = helper.getNewLabel(member, card.getId());
        Color color = new Color();
        color.setLabelId(label.getId());
        color.setRed(1);
        color.setGreen(2);
        color.setBlue(3);
        Color testColor = service.create(color);
        assertNotNull(testColor);
        UUID id = testColor.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@ColorT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = helper.getNewLabel(member, card.getId());
        Color updateColor = new Color();
        updateColor.setLabelId(label.getId());
        updateColor.setRed(1);
        updateColor.setGreen(2);
        updateColor.setBlue(3);
        Color color = service.create(updateColor);
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
    public void updateFailure() {
        Color testColor = new Color();
        testColor.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testColor),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Color with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
