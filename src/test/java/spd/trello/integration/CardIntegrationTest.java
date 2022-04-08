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

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardIntegrationTest extends AbstractIntegrationTest<Card> {
    private final String URL_TEMPLATE = "/cards";

    @Autowired
    private IntegrationHelper helper;

    @Autowired
    private ReminderScheduler reminderScheduler;

    @Test
    public void create() throws Exception {
        CardList cardList = helper.getNewCardList("create@CIT");
        Reminder reminder = helper.getNewReminder(cardList.getCreatedBy());
        Card card = new Card();
        card.setCreatedBy(cardList.getCreatedBy());
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
                () -> assertTrue(getValue(mvcResult, "$.createdDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
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
    public void createFailure() throws Exception {
        Card entity = new Card();
        MvcResult mvcResult = super.create(URL_TEMPLATE, entity);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        Card firstCard = helper.getNewCard("1findAll@CIT");
        Card secondCard = helper.getNewCard("2findAll@CIT");
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
        Card card = helper.getNewCard("findById@CIT");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, card.getId());
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(card.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
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
        Card card = helper.getNewCard("deleteById@CIT");
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
        Card card = helper.getNewCard("update@CIT");
        Member secondMember = helper.getNewMember("2update@CIT");
        card.setUpdatedBy(card.getCreatedBy());
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
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(card.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertTrue(getValue(mvcResult, "$.updatedDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
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
    public void updateFailure() throws Exception {
        Card firstCard = helper.getNewCard("1updateFailure@CardIntegrationTest");
        firstCard.setName(null);
        firstCard.setUpdatedBy(firstCard.getCreatedBy());

        Card secondCard = new Card();
        secondCard.setId(firstCard.getId());

        MvcResult firstMvcResult = super.update(URL_TEMPLATE, firstCard.getId(), firstCard);
        MvcResult secondMvcResult = super.update(URL_TEMPLATE, secondCard.getId(), secondCard);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), secondMvcResult.getResponse().getStatus())
        );
    }

    @Test
    public void reminder() throws Exception {
        Card card = helper.getNewCard("reminder@CardIntegrationTest");
        Reminder reminder = card.getReminder();
        reminder.setActive(true);
        reminder.setRemindOn(LocalDateTime.now());
        super.update(URL_TEMPLATE, card.getId(), card);
        reminderScheduler.runReminder();
        MvcResult mvcResult = super.findById(URL_TEMPLATE, card.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.reminder.active"))
        );
    }
}
