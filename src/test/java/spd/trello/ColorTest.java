package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.ColorRepository;
import spd.trello.services.ColorService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static spd.trello.Helper.*;
import static spd.trello.Helper.getNewCard;

public class ColorTest extends BaseTest{
    private final ColorService service = context.getBean(ColorService.class);

    @Test
    public void successCreate() {
        User user = getNewUser("successCreate@ColorT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Label label = getNewLabel(member, card.getId());
        Color testColor = service.create(member, label.getId(), 1, 2, 3);
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
        User user = getNewUser("findAll@ColorT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Label firstLabel = getNewLabel(member, card.getId());
        Label secondLabel = getNewLabel(member, card.getId());
        Color testFirstColor = service.create(member, firstLabel.getId(), 1, 2, 3);
        Color testSecondColor = service.create(member, secondLabel.getId(), 4, 5, 6);
        assertNotNull(testFirstColor);
        assertNotNull(testSecondColor);
        List<Color> testColors = service.findAll();
        assertAll(
                () -> assertTrue(testColors.contains(testFirstColor)),
                () -> assertTrue(testColors.contains(testSecondColor))
        );
    }

    @Test
    public void createFailure() {
        User user = getNewUser("createFailure@ColorT");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, 1, 2, 3),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Color doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
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
        User user = getNewUser("delete@ColorT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Label label = getNewLabel(member, card.getId());
        Color testColor = service.create(member, label.getId(), 1, 2, 3);
        assertNotNull(testColor);
        UUID id = testColor.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = getNewUser("update@ColorT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Label label = getNewLabel(member, card.getId());
        Color color = service.create(member, label.getId(), 1, 2, 3);
        assertNotNull(color);
        color.setRed(4);
        color.setGreen(5);
        color.setBlue(6);
        Color testColor = service.update(member, color);
        assertAll(
                () -> assertEquals(4, testColor.getRed()),
                () -> assertEquals(5, testColor.getGreen()),
                () -> assertEquals(6, testColor.getBlue())
        );
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Color testColor = new Color();
        testColor.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testColor),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Color with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
