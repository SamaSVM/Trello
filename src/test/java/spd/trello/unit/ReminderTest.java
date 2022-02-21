package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
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

    @Test
    public void create() {
        Reminder reminder = new Reminder();
        reminder.setCreatedBy("user@RT");
        reminder.setStart(Date.valueOf(LocalDate.of(2220, 1, 1)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 2, 2)));
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder testReminder = service.save(reminder);

        assertNotNull(testReminder);
        assertAll(
                () -> assertEquals("user@RT", testReminder.getCreatedBy()),
                () -> assertNull(testReminder.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getCreatedDate()),
                () -> assertNull(testReminder.getUpdatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2220, 1, 1)), testReminder.getStart()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 2, 2)), testReminder.getEnd()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 1, 1)), testReminder.getRemindOn()),
                () -> assertTrue(testReminder.getActive())
        );
    }

    @Test
    public void findAll() {
        Reminder firstReminder = new Reminder();
        firstReminder.setCreatedBy("user@RT");
        firstReminder.setStart(Date.valueOf(LocalDate.of(2222, 1, 1)));
        firstReminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        firstReminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder testFirstReminder = service.save(firstReminder);

        Reminder secondReminder = new Reminder();
        secondReminder.setCreatedBy("user@RT");
        secondReminder.setStart(Date.valueOf(LocalDate.of(2222, 2, 2)));
        secondReminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 2, 2)));
        secondReminder.setEnd(Date.valueOf(LocalDate.of(2222, 2, 2)));
        Reminder testSecondReminder = service.save(secondReminder);

        assertNotNull(testFirstReminder);
        assertNotNull(testSecondReminder);
        List<Reminder> testReminders = service.getAll();
        assertAll(
                () -> assertTrue(testReminders.contains(testFirstReminder)),
                () -> assertTrue(testReminders.contains(testSecondReminder))
        );
    }

    @Test
    public void findById() {
        Reminder reminder = new Reminder();
        reminder.setCreatedBy("user@RT");
        reminder.setStart(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        service.save(reminder);

        Reminder testReminder = service.getById(reminder.getId());
        assertEquals(reminder, testReminder);
    }

    @Test
    public void delete() {
        Reminder reminder = new Reminder();
        reminder.setCreatedBy("user@RT");
        reminder.setStart(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder testReminder = service.save(reminder);

        assertNotNull(testReminder);
        service.delete(testReminder.getId());
        assertFalse(service.getAll().contains(testReminder));
    }

    @Test
    public void update() {
        Reminder updateReminder = new Reminder();
        updateReminder.setCreatedBy("user@RT");
        updateReminder.setStart(Date.valueOf(LocalDate.of(2222, 1, 1)));
        updateReminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 1, 1)));
        updateReminder.setEnd(Date.valueOf(LocalDate.of(2222, 1, 1)));
        Reminder reminder = service.save(updateReminder);

        assertNotNull(reminder);
        reminder.setUpdatedBy("user@RT");
        reminder.setStart(Date.valueOf(LocalDate.of(2222, 2, 2)));
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 2, 2)));
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 3, 3)));
        reminder.setActive(false);
        Reminder testReminder = service.update(reminder);

        assertAll(
                () -> assertEquals("user@RT", testReminder.getCreatedBy()),
                () -> assertEquals("user@RT", testReminder.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getUpdatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 2, 2)), testReminder.getStart()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 2, 2)), testReminder.getEnd()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 3, 3)), testReminder.getRemindOn()),
                () -> assertFalse(testReminder.getActive())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Reminder()),
                "no exception"
        );
        assertTrue(ex.getMessage().contains("could not execute statement;"));
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
        assertEquals("No class spd.trello.domain.Reminder entity with id " + id + " exists!", ex.getMessage());
    }
}
