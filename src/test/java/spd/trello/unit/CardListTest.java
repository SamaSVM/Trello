package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Board;
import spd.trello.domain.CardList;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.CardListService;

import java.sql.Date;
import java.time.LocalDate;
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
        Board board = helper.getNewBoard("successCreate@CLT");

        CardList cardList = new CardList();
        cardList.setCreatedBy(board.getCreatedBy());
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
        CardList testFirstCardList = helper.getNewCardList("findAll@CLT");
        CardList testSecondCardList = helper.getNewCardList("2findAll@CLT");

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
        CardList cardList = helper.getNewCardList("findById@CLT");

        CardList testCardList = service.getById(cardList.getId());
        assertEquals(cardList, testCardList);
    }

    @Test
    public void delete() {
        CardList cardList = helper.getNewCardList("delete@CLT");

        assertNotNull(cardList);
        service.delete(cardList.getId());
        assertFalse(service.getAll().contains(cardList));
    }

    @Test
    public void update() {
        CardList cardList = helper.getNewCardList("update@CLT");

        assertNotNull(cardList);
        cardList.setUpdatedBy(cardList.getCreatedBy());
        cardList.setName("newCardList");
        cardList.setArchived(true);
        CardList testCardList = service.update(cardList);

        assertNotNull(testCardList);
        assertAll(
                () -> assertEquals(cardList.getCreatedBy(), testCardList.getCreatedBy()),
                () -> assertEquals(cardList.getUpdatedBy(), testCardList.getUpdatedBy()),
                () -> assertTrue(testCardList.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertTrue(testCardList.getUpdatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals(cardList.getName(), testCardList.getName()),
                () -> assertTrue(testCardList.getArchived()),
                () -> assertEquals(cardList.getBoardId(), testCardList.getBoardId())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new CardList()),
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
        assertEquals("No class spd.trello.domain.CardList entity with id " + id + " exists!", ex.getMessage());
    }
}
