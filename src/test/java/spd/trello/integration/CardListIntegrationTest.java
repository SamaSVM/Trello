package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Board;
import spd.trello.domain.CardList;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardListIntegrationTest extends AbstractIntegrationTest<CardList> {
    private final String URL_TEMPLATE = "/cardlists";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Board board = helper.getNewBoard("create@CardListIntegrationTest");
        CardList cardList = new CardList();
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setBoardId(board.getId());
        cardList.setName("name");
        cardList.setArchived(true);
        MvcResult mvcResult = super.create(URL_TEMPLATE, cardList);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(cardList.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertTrue(getValue(mvcResult, "$.createdDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(cardList.getName(), getValue(mvcResult, "$.name")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(board.getId().toString(), getValue(mvcResult, "$.boardId"))
        );
    }

    @Test
    public void createFailure() throws Exception {
        CardList entity = new CardList();
        MvcResult mvcResult = super.create(URL_TEMPLATE, entity);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        CardList firstCardList = helper.getNewCardList("1findAll@CardListIntegrationTest");
        CardList secondCardList = helper.getNewCardList("2findAll@CardListIntegrationTest");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<CardList> testCardLists = helper.getCardListsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testCardLists.contains(firstCardList)),
                () -> assertTrue(testCardLists.contains(secondCardList))
        );
    }

    @Test
    public void findById() throws Exception {
        CardList cardList = helper.getNewCardList("findById@CardListIntegrationTest");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, cardList.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(cardList.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(cardList.getName(), getValue(mvcResult, "$.name")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(cardList.getBoardId().toString(), getValue(mvcResult, "$.boardId"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        CardList cardList = helper.getNewCardList("deleteById@CardListIntegrationTest");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, cardList.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<CardList> testCardLists = helper.getCardListsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testCardLists.contains(cardList))
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
        CardList cardList = helper.getNewCardList("update@CardListIntegrationTest");
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setName("new Name");
        cardList.setArchived(true);
        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(cardList.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(cardList.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertTrue(getValue(mvcResult, "$.updatedDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals(cardList.getName(), getValue(mvcResult, "$.name")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(cardList.getBoardId().toString(), getValue(mvcResult, "$.boardId"))
        );
    }

    @Test
    public void updateFailure() throws Exception {
        CardList firstCardList = helper.getNewCardList("1updateFailure@CardListIntegrationTest");
        firstCardList.setName(null);
        firstCardList.setUpdatedBy(firstCardList.getCreatedBy());

        CardList secondCardList = new CardList();
        secondCardList.setId(firstCardList.getId());

        MvcResult firstMvcResult = super.update(URL_TEMPLATE, firstCardList.getId(), firstCardList);
        MvcResult secondMvcResult = super.update(URL_TEMPLATE, secondCardList.getId(), secondCardList);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), secondMvcResult.getResponse().getStatus())
        );
    }
}
