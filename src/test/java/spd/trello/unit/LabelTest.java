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
        User user = helper.getNewUser("create@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Label label = new Label();
        label.setCardId(card.getId());
        label.setName("name");
        Label testLabel = service.save(label);

        assertNotNull(testLabel);
        assertAll(
                () -> assertEquals("name", testLabel.getName()),
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

        Label firstLabel = new Label();
        firstLabel.setCardId(card.getId());
        firstLabel.setName("1Label");
        Label testFirstLabel = service.save(firstLabel);
        assertNotNull(testFirstLabel);

        Label secondLabel = new Label();
        secondLabel.setCardId(card.getId());
        secondLabel.setName("2Label");
        Label testSecondLabel = service.save(secondLabel);
        assertNotNull(testSecondLabel);

        List<Label> testLabels = service.getAll();
        assertAll(
                () -> assertTrue(testLabels.contains(testFirstLabel)),
                () -> assertTrue(testLabels.contains(testSecondLabel))
        );
    }


    @Test
    public void findById() {
        User user = helper.getNewUser("findById@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Label label = new Label();
        label.setCardId(card.getId());
        label.setName("Label");
        service.save(label);

        Label testLabel = service.getById(label.getId());
        assertEquals(label, testLabel);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Label label = new Label();
        label.setCardId(card.getId());
        label.setName("Name");
        Label testLabel = service.save(label);

        assertNotNull(testLabel);
        service.delete(testLabel.getId());
        assertFalse(service.getAll().contains(testLabel));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@LT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Label updateLabel = new Label();
        updateLabel.setCardId(card.getId());
        updateLabel.setName("Name");
        Label label = service.save(updateLabel);

        assertNotNull(label);
        label.setName("newName");
        Label testLabel = service.update(label);
        assertEquals("newName", testLabel.getName());
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
