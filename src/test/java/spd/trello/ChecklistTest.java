package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.ChecklistService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChecklistTest {
    @Autowired
    private ChecklistService service;

    @Autowired
    private Helper helper;

    @Test
    public void create() {
        User user = helper.getNewUser("successCreate@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Checklist checklist = new Checklist();
        checklist.setCardId(card.getId());
        checklist.setCreatedBy(user.getEmail());
        checklist.setName("testName");
        Checklist testChecklist = service.save(checklist);

        assertNotNull(testChecklist);
        assertAll(
                () -> assertEquals(user.getEmail(), testChecklist.getCreatedBy()),
                () -> assertNull(testChecklist.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getCreatedDate()),
                () -> assertNull(testChecklist.getUpdatedDate()),
                () -> assertEquals("testName", testChecklist.getName()),
                () -> assertEquals(card.getId(), testChecklist.getCardId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("findAll@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card firstCard = helper.getNewCard(member, cardList.getId());
        Card secondCard = helper.getNewCard(member, cardList.getId());

        Checklist firstChecklist = new Checklist();
        firstChecklist.setCardId(firstCard.getId());
        firstChecklist.setCreatedBy(user.getEmail());
        firstChecklist.setName("1Checklist");
        Checklist testFirstChecklist = service.save(firstChecklist);

        Checklist secondChecklist = new Checklist();
        secondChecklist.setCardId(secondCard.getId());
        secondChecklist.setCreatedBy(user.getEmail());
        secondChecklist.setName("2Checklist");
        Checklist testSecondChecklist = service.save(secondChecklist);

        assertNotNull(testFirstChecklist);
        assertNotNull(testSecondChecklist);
        List<Checklist> testChecklists = service.getAll();
        assertAll(
                () -> assertTrue(testChecklists.contains(testFirstChecklist)),
                () -> assertTrue(testChecklists.contains(testSecondChecklist))
        );
    }

    @Test
    public void findById() {
        User user = helper.getNewUser("findById@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Checklist checklist = new Checklist();
        checklist.setCardId(card.getId());
        checklist.setCreatedBy(user.getEmail());
        checklist.setName("testName");
        service.save(checklist);

        Checklist testChecklist = service.getById(checklist.getId());
        assertEquals(checklist, testChecklist);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Checklist checklist = new Checklist();
        checklist.setCardId(card.getId());
        checklist.setCreatedBy(user.getEmail());
        checklist.setName("Checklist");
        Checklist testChecklist = service.save(checklist);

        assertNotNull(testChecklist);
        service.delete(testChecklist.getId());
        assertFalse(service.getAll().contains(testChecklist));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Checklist updateChecklist = new Checklist();
        updateChecklist.setCardId(card.getId());
        updateChecklist.setCreatedBy(user.getEmail());
        updateChecklist.setName("Checklist");
        Checklist checklist = service.save(updateChecklist);

        assertNotNull(checklist);
        checklist.setUpdatedBy(user.getEmail());
        checklist.setName("newName");
        Checklist testChecklist = service.update(checklist);

        assertAll(
                () -> assertEquals(user.getEmail(), testChecklist.getCreatedBy()),
                () -> assertEquals(user.getEmail(), testChecklist.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getUpdatedDate()),
                () -> assertEquals("newName", testChecklist.getName())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Checklist()),
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
        assertEquals("No class spd.trello.domain.Checklist entity with id " + id + " exists!", ex.getMessage());
    }
}
