package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.ReminderScheduler;
import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.Reminder;
import spd.trello.services.BoardService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardIntegrationTest extends AbstractIntegrationTest<Card> {
    private final String URL_TEMPLATE = "/cards";

    @Autowired
    private IntegrationHelper helper;

    @Autowired
    private ReminderScheduler reminderScheduler;

    @Autowired
    private BoardService boardService;

    @Test
    public void create() throws Exception {
        CardList cardList = helper.getNewCardList("create@CIT.com");
        Reminder reminder = helper.getNewReminder(cardList.getCreatedBy());
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setCardListId(cardList.getId());
        card.setName("name");
        card.setReminder(reminder);
        card.setMembersId(helper.getMembersIdFromCardList(cardList));
        MvcResult mvcResult = super.create(URL_TEMPLATE, card);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(card.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(card.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(card.getName(), getValue(mvcResult, "$.name")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(cardList.getId().toString(), getValue(mvcResult, "$.cardListId")),
                () -> assertEquals(card.getMembersId(), testMembersId),
                () -> assertNotNull(getValue(mvcResult, "$.reminder")),
                () -> assertTrue(
                        helper.getIdsFromJson(getValue(mvcResult, "$.checklists").toString()).isEmpty()),
                () -> assertTrue(helper.getIdsFromJson(getValue(mvcResult, "$.labels").toString()).isEmpty()),
                () -> assertTrue(helper.getIdsFromJson(getValue(mvcResult, "$.comments").toString()).isEmpty()),
                () -> assertTrue(
                        helper.getIdsFromJson(getValue(mvcResult, "$.attachments").toString()).isEmpty())
        );
    }

    @Test
    public void findAll() throws Exception {
        Card firstCard = helper.getNewCard("1findAll@CIT.com");
        Card secondCard = helper.getNewCard("2findAll@CIT.com");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Card> testCards = helper.getCardsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testCards.contains(firstCard)),
                () -> assertTrue(testCards.contains(secondCard))
        );
    }

    @Test
    public void findById() throws Exception {
        Card card = helper.getNewCard("findById@CIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, card.getId());
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(card.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(card.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(card.getName(), getValue(mvcResult, "$.name")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(card.getCardListId().toString(), getValue(mvcResult, "$.cardListId")),
                () -> assertEquals(card.getMembersId(), testMembersId),
                () -> assertNotNull(getValue(mvcResult, "$.reminder")),
                () -> assertTrue(
                        helper.getIdsFromJson(getValue(mvcResult, "$.checklists").toString()).isEmpty()),
                () -> assertTrue(helper.getIdsFromJson(getValue(mvcResult, "$.labels").toString()).isEmpty()),
                () -> assertTrue(helper.getIdsFromJson(getValue(mvcResult, "$.comments").toString()).isEmpty()),
                () -> assertTrue(
                        helper.getIdsFromJson(getValue(mvcResult, "$.attachments").toString()).isEmpty())
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        Card card = helper.getNewCard("deleteById@CIT.com");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, card.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Card> testCards = helper.getCardsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testCards.contains(card))
        );
    }

    @Test
    public void deleteByIdFailure() throws Exception {
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, UUID.randomUUID());

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus())
        );
    }

    @Test
    public void update() throws Exception {
        Card card = helper.getNewCard("update@CIT.com");
        Member secondMember = helper.getNewMember("2update@CIT.com");
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now().withNano(0));
        card.setName("new Name");
        card.setArchived(true);
        card.setDescription("new description");
        Set<UUID> membersId = card.getMembersId();
        membersId.add(secondMember.getId());
        card.setMembersId(membersId);
        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(card.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(card.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(card.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(card.getUpdatedDate().toString(), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(card.getName(), getValue(mvcResult, "$.name")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(card.getCardListId().toString(), getValue(mvcResult, "$.cardListId")),
                () -> assertEquals(card.getMembersId(), testMembersId),
                () -> assertNotNull(getValue(mvcResult, "$.reminder")),
                () -> assertTrue(
                        helper.getIdsFromJson(getValue(mvcResult, "$.checklists").toString()).isEmpty()),
                () -> assertTrue(helper.getIdsFromJson(getValue(mvcResult, "$.labels").toString()).isEmpty()),
                () -> assertTrue(helper.getIdsFromJson(getValue(mvcResult, "$.comments").toString()).isEmpty()),
                () -> assertTrue(
                        helper.getIdsFromJson(getValue(mvcResult, "$.attachments").toString()).isEmpty())
        );
    }

    @Test
    public void reminder() throws Exception {
        Card card = helper.getNewCard("reminder@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        reminder.setActive(true);
        reminder.setRemindOn(LocalDateTime.now().minusMinutes(1));

        super.update(URL_TEMPLATE, card.getId(), card);
        reminderScheduler.runReminder();
        MvcResult mvcResult = super.findById(URL_TEMPLATE, card.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.reminder.active"))
        );
    }

    @Test
    public void validationCreate() throws Exception {
        Card card = new Card();
        card.setCreatedBy("c");
        card.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        card.setName("n");
        card.setDescription("d");
        card.setCardListId(UUID.randomUUID());
        Set<UUID> memberId = new HashSet<>();
        memberId.add(UUID.randomUUID());
        card.setMembersId(memberId);
        card.setReminder(helper.getNewReminder("validationCreate@CIT.com"));

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String descriptionException = "The description field must be between 2 and 255 characters long. \n";
        String cardListIdException = "The cardListId field must belong to a CardList.";
        String memberIdException = " - memberId must belong to the member. \n";

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(descriptionException)),
                () -> assertTrue(exceptionMessage.contains(cardListIdException)),
                () -> assertTrue(exceptionMessage.contains(memberIdException))
        );
    }

    @Test
    public void archivedCardCreate() throws Exception {
        CardList cardList = helper.getNewCardList("archivedCardCreate@CIT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setArchived(true);

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("You cannot create an archived card.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldCreate() throws Exception {
        CardList cardList = helper.getNewCardList("nullCreatedByFieldCreate@CIT.com");
        Card card = new Card();
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullCreatedByFieldC1@CIT.com"));

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldCreate() throws Exception {
        CardList cardList = helper.getNewCardList("nullCreatedDate@CIT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullCreatedDate1@CIT.com"));

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameFieldCreate() throws Exception {
        CardList cardList = helper.getNewCardList("nullNameFieldCreate@CIT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullNameFieldC1@CIT.com"));

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullMemberCreate() throws Exception {
        CardList cardList = helper.getNewCardList("nullMemberCreate@CIT.com");
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("name");
        card.setDescription("description");
        card.setCardListId(cardList.getId());
        card.setReminder(helper.getNewReminder("nullMemberCreate1@CIT.com"));

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The resource must belong to at least one member!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void validationReminderCreate() throws Exception {
        CardList cardList = helper.getNewCardList("validationReminderCreate@CIT.com");

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

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(laterException)),
                () -> assertTrue(exceptionMessage.contains(remindOnException))
        );
    }

    @Test
    public void nullCreatedByReminderCreate() throws Exception {
        CardList cardList = helper.getNewCardList("nullCreatedByReminderCreate@CIT.com");

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

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateReminderCreate() throws Exception {
        CardList cardList = helper.getNewCardList("nullCreatedDateReminderCreate@CIT.com");

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

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void validationUpdate() throws Exception {
        Card card = helper.getNewCard("validationUpdate@CIT.com");
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

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdByUpdateException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(updatedByException)),
                () -> assertTrue(exceptionMessage.contains(updatedDateException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(descriptionException)),
                () -> assertTrue(exceptionMessage.contains(cardException)),
                () -> assertTrue(exceptionMessage.contains(memberIdException))
        );
    }

    @Test
    public void nonExistentCardUpdate() throws Exception {
        Card card = helper.getNewCard("nonExistentCardListUpdate@CIT.com");
        card.setId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent card!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void archivedCardUpdate() throws Exception {
        Card card = helper.getNewCard("archivedCardU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setArchived(true);
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);
        super.update(URL_TEMPLATE, card.getId(), card);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Archived Card cannot be updated.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldsUpdate() throws Exception {
        Card card = helper.getNewCard("nullCreatedByFieldsUpdate@CIT.com");
        card.setCreatedBy(null);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldsUpdate() throws Exception {
        Card card = helper.getNewCard("nullCreatedDFU@CIT.com");
        card.setCreatedDate(null);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);
        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByFieldsUpdate() throws Exception {
        Card card = helper.getNewCard("nullUpdatedByFieldsUpdate@CIT.com");
        card.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() throws Exception {
        Card card = helper.getNewCard("nullUpdatedDateFU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullMembersUpdate() throws Exception {
        Card card = helper.getNewCard("nullMembersUpdate@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setMembersId(new HashSet<>());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The resource must belong to at least one member!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameUpdate() throws Exception {
        Card card = helper.getNewCard("nullNameUpdate@CIT.com");
        card.setName(null);
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void validationReminderUpdate() throws Exception {
        Card card = helper.getNewCard("validationReminderUpdate@CIT.com");
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

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdByFieldException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(updatedByException)),
                () -> assertTrue(exceptionMessage.contains(updatedDateException)),
                () -> assertTrue(exceptionMessage.contains(timeException)),
                () -> assertTrue(exceptionMessage.contains(remindOnException))
        );
    }

    @Test
    public void validationInactiveReminderUpdate() throws Exception {
        Card card = helper.getNewCard("validationInactiveRU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        reminder.setActive(false);
        card.setReminder(reminder);
        super.update(URL_TEMPLATE, card.getId(), card);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update an inactive remainder. \n",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nonExistReminderUpdate() throws Exception {
        Card card = helper.getNewCard("nonExistRU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setReminder(new Reminder());

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent reminder!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByReminderUpdate() throws Exception {
        Card card = helper.getNewCard("nullCreatedByRU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        reminder.setCreatedBy(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateReminderUpdate() throws Exception {
        Card card = helper.getNewCard("nullCreatedDateRU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        reminder.setUpdatedDate(LocalDateTime.now());
        reminder.setCreatedDate(null);
        card.setReminder(reminder);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByReminderUpdate() throws Exception {
        Card card = helper.getNewCard(" nullUpdatedByRU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedDate(LocalDateTime.now());
        card.setReminder(reminder);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateReminderUpdate() throws Exception {
        Card card = helper.getNewCard("nullUpdatedDateRU@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());

        Reminder reminder = card.getReminder();
        reminder.setUpdatedBy(reminder.getCreatedBy());
        card.setReminder(reminder);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullReminderCreate() throws Exception {
        CardList cardList = helper.getNewCardList("nullReminderCreate@CIT.com");

        Card card = new Card();
        card.setReminder(null);
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setCreatedDate(LocalDateTime.now().withNano(0));
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);

        MvcResult mvcResult = super.create(URL_TEMPLATE, card);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Reminder not found!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullReminderUpdate() throws Exception {
        Card card = helper.getNewCard("nullReminderUpdate@CIT.com");
        card.setUpdatedBy(card.getCreatedBy());
        card.setUpdatedDate(LocalDateTime.now());
        card.setReminder(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, card.getId(), card);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Reminder not found!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }
}
