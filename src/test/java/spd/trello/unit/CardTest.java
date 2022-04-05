package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.Reminder;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.BoardService;
import spd.trello.services.CardService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardTest {
    @Autowired
    private CardService service;

    @Autowired
    private BoardService boardService;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        CardList cardList = helper.getNewCardList("create@CardT");

        Reminder reminder = new Reminder();
        reminder.setCreatedBy(cardList.getCreatedBy());
        reminder.setCreatedDate(LocalDateTime.now());
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now());
        reminder.setRemindOn(LocalDateTime.now());

        Card card = new Card();
        card.setReminder(reminder);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);
        Card testCard = service.save(card);

        assertNotNull(testCard);
        assertAll(
                () -> assertEquals(cardList.getCreatedBy(), testCard.getCreatedBy()),
                () -> assertNull(testCard.getUpdatedBy()),
                () -> assertTrue(testCard.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(testCard.getUpdatedDate()),
                () -> assertEquals(card.getName(), testCard.getName()),
                () -> assertEquals(card.getDescription(), testCard.getDescription()),
                () -> assertFalse(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId()),
                () -> assertEquals(1, testCard.getMembersId().size()),
                () -> assertEquals(reminder, testCard.getReminder())
        );
    }

    @Test
    public void findAll() {
        Card testFirstCard = helper.getNewCard("findAll@CardT");
        Card testSecondCard = helper.getNewCard("2findAll@CardT");

        assertNotNull(testFirstCard);
        assertNotNull(testSecondCard);
        List<Card> testCard = service.getAll();
        assertAll(
                () -> assertTrue(testCard.contains(testFirstCard)),
                () -> assertTrue(testCard.contains(testSecondCard))
        );
    }

    @Test
    public void findById() {
        Card card = helper.getNewCard("findById@CardT");

        Card testCard = service.getById(card.getId());
        assertEquals(card, testCard);
    }

    @Test
    public void delete() {
        Card card = helper.getNewCard("delete@CardT");

        assertNotNull(card);
        service.delete(card.getId());
        assertFalse(service.getAll().contains(card));
    }

    @Test
    public void update() {
        Card card = helper.getNewCard("update@CardT");
        Member secondMember = helper.getNewMember("2update@CardT");

        assertNotNull(card);
        card.setUpdatedBy(card.getCreatedBy());
        card.setName("newCard");
        card.setDescription("newDescription");
        card.setArchived(true);
        Set<UUID> membersId = card.getMembersId();
        membersId.add(secondMember.getId());
        card.setMembersId(membersId);
        Card testCard = service.update(card);

        assertAll(
                () -> assertEquals(card.getCreatedBy(), testCard.getCreatedBy()),
                () -> assertEquals(card.getUpdatedBy(), testCard.getUpdatedBy()),
                () -> assertTrue(testCard.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertTrue(testCard.getUpdatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals("newCard", testCard.getName()),
                () -> assertEquals("newDescription", testCard.getDescription()),
                () -> assertTrue(testCard.getArchived()),
                () -> assertEquals(card.getCardListId(), testCard.getCardListId()),
                () -> assertEquals(2, testCard.getMembersId().size()),
                () -> assertTrue(testCard.getMembersId().contains(secondMember.getId()))
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Card()),
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
        assertEquals("No class spd.trello.domain.Card entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void createReminder() {
        CardList cardList = helper.getNewCardList("createReminder@CardT");

        Reminder reminder = new Reminder();
        reminder.setCreatedBy(cardList.getCreatedBy());
        reminder.setCreatedDate(LocalDateTime.now());
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now());
        reminder.setRemindOn(LocalDateTime.now());
        reminder.setActive(true);

        Card card = new Card();
        card.setReminder(reminder);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);
        service.save(card);
        service.runReminder();
        Card testCard = service.getById(card.getId());

        assertNotNull(testCard);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd HH mm ss");
        assertAll(
                () -> assertEquals(reminder.getCreatedBy(), testCard.getReminder().getCreatedBy()),
                () -> assertEquals(reminder.getCreatedDate().format(dtf), testCard.getReminder().getCreatedDate().format(dtf)),
                () -> assertEquals(reminder.getStart().format(dtf), testCard.getReminder().getStart().format(dtf)),
                () -> assertEquals(reminder.getEnd().format(dtf), testCard.getReminder().getEnd().format(dtf)),
                () -> assertEquals(reminder.getRemindOn().format(dtf), testCard.getReminder().getRemindOn().format(dtf)),
                () -> assertFalse(testCard.getReminder().getActive())
        );
    }

    @Test
    public void updateReminder() {
        Card card = helper.getNewCard("updateReminder@CardT");
        card.setUpdatedBy(card.getCreatedBy());

        Reminder reminder = card.getReminder();
        reminder.setCreatedBy(card.getCreatedBy());
        reminder.setCreatedDate(LocalDateTime.now());
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now());
        reminder.setRemindOn(LocalDateTime.now());
        reminder.setActive(true);
        card.setReminder(reminder);

        service.update(card);
        service.runReminder();
        Card testCard = service.getById(card.getId());

        assertNotNull(testCard);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd HH mm ss");
        assertAll(
                () -> assertEquals(reminder.getCreatedBy(), testCard.getReminder().getCreatedBy()),
                () -> assertEquals(reminder.getCreatedDate().format(dtf), testCard.getReminder().getCreatedDate().format(dtf)),
                () -> assertEquals(reminder.getStart().format(dtf), testCard.getReminder().getStart().format(dtf)),
                () -> assertEquals(reminder.getEnd().format(dtf), testCard.getReminder().getEnd().format(dtf)),
                () -> assertEquals(reminder.getRemindOn().format(dtf), testCard.getReminder().getRemindOn().format(dtf)),
                () -> assertFalse(testCard.getReminder().getActive())
        );
    }
}
