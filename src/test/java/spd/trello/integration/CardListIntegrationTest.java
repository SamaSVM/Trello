package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Board;
import spd.trello.domain.CardList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardListIntegrationTest extends AbstractIntegrationTest<CardList> {
    private final String URL_TEMPLATE = "/cardlists";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Board board = helper.getNewBoard("create@CLTest.com");
        CardList cardList = new CardList();
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setCreatedDate(LocalDateTime.now().withNano(0));
        cardList.setBoardId(board.getId());
        cardList.setName("name");
        MvcResult mvcResult = super.create(URL_TEMPLATE, cardList);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(cardList.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(cardList.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(cardList.getName(), getValue(mvcResult, "$.name")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(board.getId().toString(), getValue(mvcResult, "$.boardId"))
        );
    }

    @Test
    public void findAll() throws Exception {
        CardList firstCardList = helper.getNewCardList("1findAll@CLTest.com");
        CardList secondCardList = helper.getNewCardList("2findAll@CLTest.com");
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
        CardList cardList = helper.getNewCardList("findById@CLTest.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, cardList.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(cardList.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(cardList.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
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
        CardList cardList = helper.getNewCardList("deleteById@CLTest.com");
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
        CardList cardList = helper.getNewCardList("update@CLTest.com");
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now().withNano(0));
        cardList.setName("new Name");
        cardList.setArchived(true);
        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(cardList.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(cardList.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(cardList.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(cardList.getUpdatedDate().toString(), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(cardList.getName(), getValue(mvcResult, "$.name")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(cardList.getBoardId().toString(), getValue(mvcResult, "$.boardId"))
        );
    }

    @Test
    public void validationCreate() throws Exception {
        Board board = helper.getNewBoard("validationCreate@CLTest.com");
        CardList cardList = new CardList();
        cardList.setName("n");
        cardList.setCreatedBy("c");
        cardList.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        cardList.setBoardId(board.getId());

        String nameException = "Name should be between 2 and 30 characters! \n";
        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";

        MvcResult mvcResult = super.create(URL_TEMPLATE, cardList);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException))
        );
    }

    @Test
    public void archivedCardListCreate() throws Exception {
        Board board = helper.getNewBoard("archivedCardListCreate@CLTest.com");
        CardList cardList = new CardList();
        cardList.setArchived(true);
        cardList.setName("name");
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setCreatedDate(LocalDateTime.now());
        cardList.setBoardId(board.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("You cannot create an archived card list.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldCreate() throws Exception {
        Board board = helper.getNewBoard("nullCreatedByFieldCreate@CLTest.com");
        CardList cardList = new CardList();
        cardList.setName("name");
        cardList.setCreatedDate(LocalDateTime.now());
        cardList.setBoardId(board.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldCreate() throws Exception {
        Board board = helper.getNewBoard("nullCreatedDateFieldCreate@CLTest.com");
        CardList cardList = new CardList();
        cardList.setName("name");
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setBoardId(board.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nonExistBoardCreate() throws Exception {
        CardList cardList = new CardList();
        cardList.setName("name");
        cardList.setCreatedBy("createdBy");
        cardList.setCreatedDate(LocalDateTime.now());
        cardList.setBoardId(UUID.randomUUID());

        MvcResult mvcResult = super.create(URL_TEMPLATE, cardList);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The boardId field must belong to a board.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void validationUpdate() throws Exception {
        CardList cardList = helper.getNewCardList("validationUpdate@CLTest.com");
        cardList.setName("n");
        cardList.setCreatedBy("c");
        cardList.setCreatedDate(LocalDateTime.now());
        cardList.setUpdatedBy("u");
        cardList.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        cardList.setBoardId(UUID.randomUUID());

        String createdBySizeException = "CreatedBy should be between 2 and 30 characters! \n";
        String updatedDateException = "The updatedDate should not be past or future. \n";
        String createdByException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String cardListException = "CardList cannot be transferred to another board. \n";
        String nameException = "Name should be between 2 and 30 characters! \n";

        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdBySizeException)),
                () -> assertTrue(exceptionMessage.contains(updatedDateException)),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(updatedByException)),
                () -> assertTrue(exceptionMessage.contains(cardListException)),
                () -> assertTrue(exceptionMessage.contains(nameException))
        );
    }

    @Test
    public void nonExistentCardListUpdate() throws Exception {
        CardList cardList = helper.getNewCardList("nonExistentCardListU@CLTest.com");
        cardList.setId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent card list!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void archivedCardListUpdate() throws Exception {
        CardList cardList = helper.getNewCardList("archivedCardListU@CLTest.com");
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now());
        cardList.setArchived(true);
        super.update(URL_TEMPLATE, cardList.getId(), cardList);

        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Archived CardList cannot be updated.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldsUpdate() throws Exception {
        CardList cardList = helper.getNewCardList("nullCreatedByFieldsUpdate@CLTest.com");
        cardList.setCreatedBy(null);
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldsUpdate() throws Exception {
        CardList cardList = helper.getNewCardList("nullCreatedDateFieldsUpdate@CLTest.com");
        cardList.setCreatedDate(null);
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByFieldsUpdate() throws Exception {
        CardList cardList = helper.getNewCardList("nullUpdatedByFieldsUpdate@CLTest.com");
        cardList.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() throws Exception {
        CardList cardList = helper.getNewCardList("nullUpdatedDateFieldsU@CLTest.com");
        cardList.setUpdatedBy(cardList.getCreatedBy());

        MvcResult mvcResult = super.update(URL_TEMPLATE, cardList.getId(), cardList);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }
}
