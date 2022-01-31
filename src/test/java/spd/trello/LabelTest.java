package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
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
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("successCreate@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label testLabel = service.create(member, card.getId(), "successCreate@LT");
        assertNotNull(testLabel);
        assertAll(
                () -> assertEquals("successCreate@LT", testLabel.getName()),
                () -> assertEquals(card.getId(), testLabel.getCardId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("findAll@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label testFirstLabel = service.create(member, card.getId(), "1Label");
        Label testSecondLabel = service.create(member, card.getId(), "2Label");
        assertNotNull(testFirstLabel);
        assertNotNull(testSecondLabel);
        List<Label> testLabels = service.findAll();
        assertAll(
                () -> assertTrue(testLabels.contains(testFirstLabel)),
                () -> assertTrue(testLabels.contains(testSecondLabel))
        );
    }

    @Test
    public void createFailure() {
        User user = helper.getNewUser("createFailure@LT");
        Member member = helper.getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Name"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Label doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Label with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label testLabel = service.create(member, card.getId(), "Name");
        assertNotNull(testLabel);
        UUID id = testLabel.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Label label = service.create(member, card.getId(), "Name");
        assertNotNull(label);
        label.setName("newName");
        Label testLabel = service.update(member, label);
        assertEquals("newName", testLabel.getName());
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Label testLabel = new Label();
        testLabel.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testLabel),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Label with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
