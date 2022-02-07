package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.services.ReminderService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReminderTest {
    @Autowired
    private ReminderService service;

    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("successCreate@RT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Reminder reminder = new Reminder();
        reminder.setCardId(card.getId());
        reminder.setCreatedBy("successCreate@RT");
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder testReminder = service.create(reminder);
        assertNotNull(testReminder);
        assertAll(
                () -> assertEquals("successCreate@RT", testReminder.getCreatedBy()),
                () -> assertNull(testReminder.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getCreatedDate()),
                () -> assertNull(testReminder.getUpdatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getStart()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 1, 1)), testReminder.getEnd()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 1, 1)), testReminder.getRemindOn()),
                () -> assertTrue(testReminder.getActive()),
                () -> assertEquals(card.getId(), testReminder.getCardId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("findAll@RT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Reminder reminder = new Reminder();
        reminder.setCardId(card.getId());
        reminder.setCreatedBy("findAll@RT");
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder testFirstReminder = service.create(reminder);
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 2, 2)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 2, 2)));
        Reminder testSecondReminder = service.create(reminder);
        assertNotNull(testFirstReminder);
        assertNotNull(testSecondReminder);
        List<Reminder> testComments = service.findAll();
        assertAll(
                () -> assertTrue(testComments.contains(testFirstReminder)),
                () -> assertTrue(testComments.contains(testSecondReminder))
        );
    }

    @Test
    public void createFailure() {
        Reminder reminder = new Reminder();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(reminder),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Reminder doesn't creates", ex.getMessage());
    }

    @Test
    public void findByIdFailure() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Reminder with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@RT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Reminder reminder = new Reminder();
        reminder.setCardId(card.getId());
        reminder.setCreatedBy("delete@RT");
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder testReminder = service.create(reminder);
        assertNotNull(testReminder);
        UUID id = testReminder.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@RT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Reminder updateReminder = new Reminder();
        updateReminder.setCardId(card.getId());
        updateReminder.setCreatedBy("update@RT");
        updateReminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        updateReminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder reminder = service.create(updateReminder);
        assertNotNull(reminder);
        reminder.setUpdatedBy("update@RT");
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 2, 2)));
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 3, 3)));
        reminder.setActive(false);
        Reminder testReminder = service.update(reminder);
        assertAll(
                () -> assertEquals("update@RT", testReminder.getCreatedBy()),
                () -> assertEquals("update@RT", testReminder.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getUpdatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getStart()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 2, 2)), testReminder.getEnd()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 3, 3)), testReminder.getRemindOn()),
                () -> assertFalse(testReminder.getActive())
        );
    }

    @Test
    public void updateFailure() {
        Reminder testReminder = new Reminder();
        testReminder.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testReminder),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Reminder with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void getAllRemindersForCard() {
        User user = helper.getNewUser("getAllRemindersForCard@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Reminder firstReminder = helper.getNewReminder(member, card.getId());
        Reminder secondReminder = helper.getNewReminder(member, card.getId());
        assertNotNull(card);
        List<Reminder> comments = service.getAllReminders(card.getId());
        assertAll(
                () -> assertTrue(comments.contains(firstReminder)),
                () -> assertTrue(comments.contains(secondReminder)),
                () -> assertEquals(2, comments.size())
        );
    }
}
