package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
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
    public void successCreate() {
        User user = helper.getNewUser("successCreate@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = new Checklist();
        checklist.setCardId(card.getId());
        checklist.setCreatedBy("successCreate@CLT");
        checklist.setName("testName");
        Checklist testChecklist = service.create(checklist);
        assertNotNull(testChecklist);
        assertAll(
                () -> assertEquals("successCreate@CLT", testChecklist.getCreatedBy()),
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
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist checklist = new Checklist();
        checklist.setCardId(card.getId());
        checklist.setCreatedBy("findAll@CLT");
        checklist.setName("1 Checklist");
        Checklist testFirstChecklist = service.create(checklist);
        checklist.setName("2 Checklist");
        Checklist testSecondChecklist = service.create(checklist);
        assertNotNull(testFirstChecklist);
        assertNotNull(testSecondChecklist);
        List<Checklist> testComments = service.findAll();
        assertAll(
                () -> assertTrue(testComments.contains(testFirstChecklist)),
                () -> assertTrue(testComments.contains(testSecondChecklist))
        );
    }

    @Test
    public void createFailure() {
        Checklist checklist = new Checklist();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(checklist),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Checklist doesn't creates", ex.getMessage());
    }

    @Test
    public void findByIdFailure() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Checklist with ID: " + uuid + " doesn't exists", ex.getMessage());
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
        checklist.setCreatedBy("delete@CLT");
        checklist.setName("Checklist");
        Checklist testChecklist = service.create(checklist);
        assertNotNull(testChecklist);
        UUID id = testChecklist.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
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
        updateChecklist.setCreatedBy("update@CLT");
        updateChecklist.setName("Checklist");
        Checklist checklist = service.create(updateChecklist);
        assertNotNull(checklist);
        checklist.setUpdatedBy("update@CLT");
        checklist.setName("newName");
        Checklist testChecklist = service.update(checklist);
        assertAll(
                () -> assertEquals("update@CLT", testChecklist.getCreatedBy()),
                () -> assertEquals("update@CLT", testChecklist.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testChecklist.getUpdatedDate()),
                () -> assertEquals("newName", testChecklist.getName())
        );
    }

    @Test
    public void updateFailure() {
        Checklist testChecklist = new Checklist();
        testChecklist.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testChecklist),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Checklist with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void getAllChecklistsForCard() {
        User user = helper.getNewUser("getAllChecklistsForCard@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Checklist firstChecklist = helper.getNewChecklist(member, card.getId());
        Checklist secondChecklist = helper.getNewChecklist(member, card.getId());
        assertNotNull(card);
        List<Checklist> checklists = service.getAllChecklists(card.getId());
        assertAll(
                () -> assertTrue(checklists.contains(firstChecklist)),
                () -> assertTrue(checklists.contains(secondChecklist)),
                () -> assertEquals(2, checklists.size())
        );
    }
}
