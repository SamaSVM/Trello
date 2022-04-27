package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Board;
import spd.trello.domain.CardList;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.services.CardListService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CardListTest {
    @Autowired
    private CardListService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void successCreate() {
        Board board = helper.getNewBoard("successCreate@CLT.com");

        CardList cardList = new CardList();
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setCreatedDate(LocalDateTime.now().withNano(0));
        cardList.setName("testCardList");
        cardList.setBoardId(board.getId());
        CardList testCardList = service.save(cardList);

        assertNotNull(testCardList);
        assertAll(
                () -> assertEquals(board.getCreatedBy(), testCardList.getCreatedBy()),
                () -> assertNull(testCardList.getUpdatedBy()),
                () -> assertTrue(testCardList.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(testCardList.getUpdatedDate()),
                () -> assertEquals(cardList.getName(), testCardList.getName()),
                () -> assertFalse(testCardList.getArchived()),
                () -> assertEquals(board.getId(), testCardList.getBoardId())
        );
    }

    @Test
    public void findAll() {
        CardList testFirstCardList = helper.getNewCardList("findAll@CLT.com");
        CardList testSecondCardList = helper.getNewCardList("2findAll@CLT.com");

        assertNotNull(testFirstCardList);
        assertNotNull(testSecondCardList);
        List<CardList> testCardLists = service.getAll();
        assertAll(
                () -> assertTrue(testCardLists.contains(testFirstCardList)),
                () -> assertTrue(testCardLists.contains(testSecondCardList))
        );
    }

    @Test
    public void findById() {
        CardList cardList = helper.getNewCardList("findById@CLT.com");

        CardList testCardList = service.getById(cardList.getId());
        assertEquals(cardList, testCardList);
    }

    @Test
    public void delete() {
        CardList cardList = helper.getNewCardList("delete@CLT.com");

        assertNotNull(cardList);
        service.delete(cardList.getId());
        assertFalse(service.getAll().contains(cardList));
    }

    @Test
    public void update() {
        CardList cardList = helper.getNewCardList("update@CLT.com");
        assertNotNull(cardList);
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now().withNano(0));
        cardList.setName("newCardList");
        cardList.setArchived(true);
        CardList testCardList = service.update(cardList);

        assertNotNull(testCardList);
        assertAll(
                () -> assertEquals(cardList.getCreatedBy(), testCardList.getCreatedBy()),
                () -> assertEquals(cardList.getUpdatedBy(), testCardList.getUpdatedBy()),
                () -> assertEquals(cardList.getCreatedDate(), testCardList.getCreatedDate()),
                () -> assertEquals(cardList.getUpdatedDate(), testCardList.getUpdatedDate()),
                () -> assertEquals(cardList.getName(), testCardList.getName()),
                () -> assertTrue(testCardList.getArchived()),
                () -> assertEquals(cardList.getBoardId(), testCardList.getBoardId())
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
        assertEquals("No class spd.trello.domain.CardList entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void validationCreate() {
        Board board = helper.getNewBoard("validationCreate@CLT.com");
        CardList cardList = new CardList();
        cardList.setName("n");
        cardList.setCreatedBy("c");
        cardList.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        cardList.setBoardId(board.getId());

        String nameException = "Name should be between 2 and 30 characters! \n";
        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(cardList), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException))
        );
    }

    @Test
    public void archivedCardListCreate() {
        Board board = helper.getNewBoard("archivedCardListCreate@CLT.com");
        CardList cardList = new CardList();
        cardList.setArchived(true);
        cardList.setName("name");
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setCreatedDate(LocalDateTime.now());
        cardList.setBoardId(board.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(cardList), "no exception"
        );
        assertEquals("You cannot create an archived card list.", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldCreate() {
        Board board = helper.getNewBoard("nullCreatedByFieldCreate@CLT.com");
        CardList cardList = new CardList();
        cardList.setName("name");
        cardList.setCreatedDate(LocalDateTime.now());
        cardList.setBoardId(board.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(cardList), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldCreate() {
        Board board = helper.getNewBoard("nullCreatedDateFieldCreate@CLT.com");
        CardList cardList = new CardList();
        cardList.setName("name");
        cardList.setCreatedBy(board.getCreatedBy());
        cardList.setBoardId(board.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(cardList), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nonExistBoardCreate() {
        CardList cardList = new CardList();
        cardList.setName("name");
        cardList.setCreatedBy("createdBy");
        cardList.setCreatedDate(LocalDateTime.now());
        cardList.setBoardId(UUID.randomUUID());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(cardList), "no exception"
        );
        assertEquals("The boardId field must belong to a board.", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
        CardList cardList = helper.getNewCardList("validationUpdate@CLT.com");
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

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(cardList), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdBySizeException)),
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(updatedByException)),
                () -> assertTrue(ex.getMessage().contains(cardListException)),
                () -> assertTrue(ex.getMessage().contains(nameException))
        );
    }

    @Test
    public void nonExistentCardListUpdate() {
        CardList cardList = helper.getNewCardList("nonExistentCardListUpdate@CLT.com");
        cardList.setId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(cardList), "no exception"
        );
        assertEquals("Cannot update non-existent card list!", ex.getMessage());
    }

    @Test
    public void archivedCardListUpdate() {
        CardList cardList = helper.getNewCardList("archivedCardListUpdate@CLT.com");
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now());
        cardList.setArchived(true);
        service.update(cardList);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(cardList), "no exception"
        );
        assertEquals("Archived CardList cannot be updated.", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldsUpdate() {
        CardList cardList = helper.getNewCardList("nullCreatedByFieldsUpdate@CLT.com");
        cardList.setCreatedBy(null);
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(cardList), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldsUpdate() {
        CardList cardList = helper.getNewCardList("nullCreatedDateFieldsUpdate@CLT.com");
        cardList.setCreatedDate(null);
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(cardList), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedByFieldsUpdate() {
        CardList cardList = helper.getNewCardList("nullUpdatedByFieldsUpdate@CLT.com");
        cardList.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(cardList), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() {
        CardList cardList = helper.getNewCardList("nullUpdatedDateFieldsUpdate@CLT.com");
        cardList.setUpdatedBy(cardList.getCreatedBy());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(cardList), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }
}
