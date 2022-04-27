package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.ReminderScheduler;
import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.Reminder;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.services.BoardService;
import spd.trello.services.CardService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
    private ReminderScheduler reminderScheduler;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        CardList cardList = helper.getNewCardList("create@CardT.com");

        Reminder reminder = new Reminder();
        reminder.setCreatedBy(cardList.getCreatedBy());
        reminder.setCreatedDate(LocalDateTime.now());
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now().plusHours(2));
        reminder.setRemindOn(LocalDateTime.now().plusHours(1));

        Card card = new Card();
        card.setReminder(reminder);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);
        Card testCard = service.save(card);

        assertNotNull(testCard);
        assertAll(
                () -> assertEquals(cardList.getCreatedBy(), testCard.getCreatedBy()),
                () -> assertNull(testCard.getUpdatedBy()),
                () -> assertEquals(card.getCreatedDate(), testCard.getCreatedDate()),
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
        Card testFirstCard = helper.getNewCard("findAll@CardT.com");
        Card testSecondCard = helper.getNewCard("2findAll@CardT.com");

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
        Card card = helper.getNewCard("findById@CardT.com");

        Card testCard = service.getById(card.getId());
        assertEquals(card, testCard);
    }

    @Test
    public void delete() {
        Card card = helper.getNewCard("delete@CardT.com");

        assertNotNull(card);
        service.delete(card.getId());
        assertFalse(service.getAll().contains(card));
    }

    @Test
    public void update() {
        Card card = helper.getNewCard("update@CardT.com");
        Member secondMember = helper.getNewMember("2update@CardT.com");

        assertNotNull(card);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now().withNano(0));
        card.setName("newCard");
        card.setDescription("newDescription");
        card.setArchived(true);
        Set<UUID> membersId = card.getMembersId();
        membersId.add(secondMember.getId());
        card.setMembersId(membersId);
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now().withNano(0));
        Card testCard = service.update(card);

        assertAll(
                () -> assertEquals(card.getCreatedBy(), testCard.getCreatedBy()),
                () -> assertEquals(card.getUpdatedBy(), testCard.getUpdatedBy()),
                () -> assertEquals(card.getCreatedDate(), testCard.getCreatedDate()),
                () -> assertEquals(card.getUpdatedDate(), testCard.getUpdatedDate()),
                () -> assertEquals("newCard", testCard.getName()),
                () -> assertEquals("newDescription", testCard.getDescription()),
                () -> assertTrue(testCard.getArchived()),
                () -> assertEquals(card.getCardListId(), testCard.getCardListId()),
                () -> assertEquals(2, testCard.getMembersId().size()),
                () -> assertTrue(testCard.getMembersId().contains(secondMember.getId()))
        );
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
        CardList cardList = helper.getNewCardList("createReminder@CardT.com");

        Reminder reminder = new Reminder();
        reminder.setCreatedBy(cardList.getCreatedBy());
        reminder.setCreatedDate(LocalDateTime.now().withNano(0));
        reminder.setStart(LocalDateTime.now().withNano(0));
        reminder.setEnd(LocalDateTime.now().plusHours(2).withNano(0));
        reminder.setRemindOn(LocalDateTime.now().plusHours(1).withNano(0));
        reminder.setActive(true);

        Card card = new Card();
        card.setReminder(reminder);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);
        service.save(card);
        reminderScheduler.runReminder();
        Card testCard = service.getById(card.getId());

        assertNotNull(testCard);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd HH mm ss");
        assertAll(
                () -> assertEquals(reminder.getCreatedBy(), testCard.getReminder().getCreatedBy()),
                () -> assertEquals(reminder.getCreatedDate().format(dtf),
                        testCard.getReminder().getCreatedDate().format(dtf)),
                () -> assertEquals(reminder.getStart().format(dtf), testCard.getReminder().getStart().format(dtf)),
                () -> assertEquals(reminder.getEnd().format(dtf), testCard.getReminder().getEnd().format(dtf)),
                () -> assertEquals(reminder.getRemindOn().format(dtf),
                        testCard.getReminder().getRemindOn().format(dtf)),
                () -> assertTrue(testCard.getReminder().getActive())
        );
    }

    @Test
    public void updateReminder() {
        Card card = helper.getNewCard("updateReminder@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now().withNano(0));

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now().withNano(0));
        reminder.setStart(LocalDateTime.now().minusHours(1));
        reminder.setEnd(LocalDateTime.now().plusHours(4));
        reminder.setRemindOn(LocalDateTime.now().plusHours(2));
        reminder.setActive(false);
        card.setReminder(reminder);

        service.update(card);
        reminderScheduler.runReminder();
        Card testCard = service.getById(card.getId());

        assertNotNull(testCard);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd HH mm ss");
        assertAll(
                () -> assertEquals(reminder.getCreatedBy(), testCard.getReminder().getCreatedBy()),
                () -> assertEquals(reminder.getCreatedDate().format(dtf),
                        testCard.getReminder().getCreatedDate().format(dtf)),
                () -> assertEquals(reminder.getStart().format(dtf), testCard.getReminder().getStart().format(dtf)),
                () -> assertEquals(reminder.getEnd().format(dtf), testCard.getReminder().getEnd().format(dtf)),
                () -> assertEquals(reminder.getRemindOn().format(dtf),
                        testCard.getReminder().getRemindOn().format(dtf)),
                () -> assertFalse(testCard.getReminder().getActive())
        );
    }

    @Test
    public void validationCreate() {
        Card card = new Card();
        card.setCreatedBy("c");
        card.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        card.setName("n");
        card.setDescription("d");
        card.setCardListId(UUID.randomUUID());
        Set<UUID> memberId = new HashSet<>();
        memberId.add(UUID.randomUUID());
        card.setMembersId(memberId);
        card.setReminder(helper.getNewReminder("validationCreate@CardT.com"));

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String descriptionException = "The description field must be between 2 and 255 characters long. \n";
        String cardListIdException = "The cardListId field must belong to a CardList.";
        String memberIdException = " - memberId must belong to the member. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(descriptionException)),
                () -> assertTrue(ex.getMessage().contains(cardListIdException)),
                () -> assertTrue(ex.getMessage().contains(memberIdException))
        );
    }

    @Test
    public void archivedCardCreate() {
        CardList cardList = helper.getNewCardList("archivedCardCreate@CardT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setArchived(true);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertEquals("You cannot create an archived card.", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldCreate() {
        CardList cardList = helper.getNewCardList("nullCreatedByFieldCreate@CardT.com");
        Card card = new Card();
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullCreatedByFieldC1@CardT.com"));

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldCreate() {
        CardList cardList = helper.getNewCardList("nullCreatedDate@CardT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullCreatedDate1@CardT.com"));
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullNameFieldCreate() {
        CardList cardList = helper.getNewCardList("nullNameFieldCreate@CardT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullNameFieldC1@CardT.com"));

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void nullMemberCreate() {
        CardList cardList = helper.getNewCardList("nullMemberCreate@CardT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullMemberCreate1@CardT.com"));

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(card), "no exception"
        );
        assertEquals("The resource must belong to at least one member!", ex.getMessage());
    }

    @Test
    public void validationReminderCreate() {
        CardList cardList = helper.getNewCardList("validationReminderCreate@CardT.com");

        Reminder reminder = new Reminder();
        reminder.setCreatedBy("c");
        reminder.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now().minusHours(1));
        reminder.setRemindOn(LocalDateTime.now().plusHours(1));

        Card card = new Card();
        card.setReminder(reminder);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String laterException = "Start cannot be later than end. \n";
        String remindOnException = "The remindOn should be between start and end. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(laterException)),
                () -> assertTrue(ex.getMessage().contains(remindOnException))
        );
    }

    @Test
    public void nullCreatedByReminderCreate() {
        CardList cardList = helper.getNewCardList("nullCreatedByReminderCreate@CardT.com");

        Reminder reminder = new Reminder();
        reminder.setCreatedDate(LocalDateTime.now());
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now().plusHours(2));
        reminder.setRemindOn(LocalDateTime.now().plusHours(1));

        Card card = new Card();
        card.setReminder(reminder);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateReminderCreate() {
        CardList cardList = helper.getNewCardList("nullCreatedDateReminderCreate@CardT.com");

        Reminder reminder = new Reminder();
        reminder.setCreatedBy(cardList.getCreatedBy());
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now().plusHours(2));
        reminder.setRemindOn(LocalDateTime.now().plusHours(1));

        Card card = new Card();
        card.setReminder(reminder);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(card), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
        Card card = helper.getNewCard("validationUpdate@CardT.com");
        card.setCreatedBy("c");
        card.setCreatedDate(LocalDateTime.now());
        card.setUpdatedBy("u");
        card.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        card.setName("n");
        card.setDescription("d");
        card.setCardListId(UUID.randomUUID());
        Set<UUID> memberId = new HashSet<>();
        memberId.add(UUID.randomUUID());
        card.setMembersId(memberId);
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdByUpdateException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String updatedDateException = "The updatedDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String descriptionException = "The description field must be between 2 and 255 characters long. \n";
        String cardException = "Card cannot be transferred to another CardList. \n";
        String memberIdException = " - memberId must belong to the member. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdByUpdateException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(updatedByException)),
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(descriptionException)),
                () -> assertTrue(ex.getMessage().contains(cardException)),
                () -> assertTrue(ex.getMessage().contains(memberIdException))
        );
    }

    @Test
    public void nonExistentCardUpdate() {
        Card card = helper.getNewCard("nonExistentCardListUpdate@CardT.com");
        card.setId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("Cannot update non-existent card!", ex.getMessage());
    }

    @Test
    public void archivedCardUpdate() {
        Card card = helper.getNewCard("archivedCardU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setArchived(true);
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);
        service.update(card);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("Archived Card cannot be updated.", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldsUpdate() {
        Card card = helper.getNewCard("nullCreatedByFieldsUpdate@CardT.com");
        card.setCreatedBy(null);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldsUpdate() {
        Card card = helper.getNewCard("nullCreatedDFU@CardT.com");
        card.setCreatedDate(null);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedByFieldsUpdate() {
        Card card = helper.getNewCard("nullUpdatedByFieldsUpdate@CardT.com");
        card.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() {
        Card card = helper.getNewCard("nullUpdatedDateFU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }

    @Test
    public void nullMembersUpdate() {
        Card card = helper.getNewCard("nullMembersUpdate@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setMembersId(new HashSet<>());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The resource must belong to at least one member!", ex.getMessage());
    }

    @Test
    public void nullNameUpdate() {
        Card card = helper.getNewCard("nullNameUpdate@CardT.com");
        card.setName(null);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void validationReminderUpdate() {
        Card card = helper.getNewCard("validationReminderUpdate@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setCreatedBy("c");
        reminder.setCreatedDate(LocalDateTime.now());
        reminder.setUpdatedBy("u");
        reminder.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        reminder.setStart(LocalDateTime.now());
        reminder.setEnd(LocalDateTime.now().minusHours(2));
        reminder.setRemindOn(LocalDateTime.now().plusHours(1));
        card.setReminder(reminder);

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdByFieldException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String updatedDateException = "The updatedDate should not be past or future. \n";
        String timeException = "Start cannot be later than end. \n";
        String remindOnException = "The remindOn should be between start and end. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdByFieldException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(updatedByException)),
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(timeException)),
                () -> assertTrue(ex.getMessage().contains(remindOnException))
        );
    }

    @Test
    public void validationInactiveReminderUpdate() {
        Card card = helper.getNewCard("validationInactiveRU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        reminder.setActive(false);
        card.setReminder(reminder);
        service.update(card);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("Cannot update an inactive remainder. \n", ex.getMessage());
    }

    @Test
    public void nonExistReminderUpdate() {
        Card card = helper.getNewCard("nonExistRU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setReminder(new Reminder());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("Cannot update non-existent reminder!", ex.getMessage());
    }

    @Test
    public void nullCreatedByReminderUpdate() {
        Card card = helper.getNewCard("nullCreatedByRU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        reminder.setCreatedBy(null);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateReminderUpdate() {
        Card card = helper.getNewCard("nullCreatedDateRU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        reminder.setCreatedDate(null);
        card.setReminder(reminder);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedByReminderUpdate() {
        Card card = helper.getNewCard(" nullUpdatedByRU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateReminderUpdate() {
        Card card = helper.getNewCard("nullUpdatedDateRU@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        card.setReminder(reminder);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(card), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }

    @Test
    public void nullReminderCreate() {
        CardList cardList = helper.getNewCardList("nullReminderCreate@CardT.com");

        Card card = new Card();
        card.setReminder(null);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(card), "no exception"
        );
        assertEquals("Reminder not found!", ex.getMessage());
    }

    @Test
    public void nullReminderUpdate() {
        Card card = helper.getNewCard("nullReminderUpdate@CardT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setReminder(null);

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.update(card), "no exception"
        );
        assertEquals("Reminder not found!", ex.getMessage());
    }
}
