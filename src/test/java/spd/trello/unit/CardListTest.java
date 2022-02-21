package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
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
        User user = helper.getNewUser("test14@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());

        CardList cardList = new CardList();
        cardList.setCreatedBy(user.getEmail());
        cardList.setName("testCardList");
        cardList.setBoardId(board.getId());
        CardList testCardList = service.save(cardList);

        assertNotNull(testCardList);
        assertAll(
                () -> assertEquals(user.getEmail(), testCardList.getCreatedBy()),
                () -> assertNull(testCardList.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCardList.getCreatedDate()),
                () -> assertNull(testCardList.getUpdatedDate()),
                () -> assertEquals("testCardList", testCardList.getName()),
                () -> assertFalse(testCardList.getArchived()),
                () -> assertEquals(board.getId(), testCardList.getBoardId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("test15@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());

        CardList firstCardList = new CardList();
        firstCardList.setCreatedBy(user.getEmail());
        firstCardList.setBoardId(board.getId());
        firstCardList.setName("1CardList");
        CardList testFirstCardList = service.save(firstCardList);

        CardList secondCardList = new CardList();
        secondCardList.setCreatedBy(user.getEmail());
        secondCardList.setBoardId(board.getId());
        secondCardList.setName("2CardList");
        CardList testSecondCardList = service.save(secondCardList);

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
        User user = helper.getNewUser("findById@CAT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());

        CardList cardList = new CardList();
        cardList.setCreatedBy(user.getEmail());
        cardList.setBoardId(board.getId());
        cardList.setName("CardList");
        service.save(cardList);

        CardList testCardList = service.getById(cardList.getId());
        assertEquals(cardList, testCardList);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test17@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());

        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setCreatedBy(user.getEmail());
        cardList.setName("testCardList");
        CardList testCardList = service.save(cardList);

        assertNotNull(testCardList);
        service.delete(testCardList.getId());
        assertFalse(service.getAll().contains(testCardList));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test18@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());

        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setCreatedBy(user.getEmail());
        cardList.setName("testCardList");
        CardList updateCardList = service.save(cardList);

        assertNotNull(updateCardList);
        updateCardList.setUpdatedBy(user.getEmail());
        updateCardList.setName("newCardList");
        updateCardList.setArchived(true);
        CardList testCardList = service.update(updateCardList);

        assertNotNull(testCardList);
        assertAll(
                () -> assertEquals("test18@mail", testCardList.getCreatedBy()),
                () -> assertEquals("test18@mail", testCardList.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCardList.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCardList.getUpdatedDate()),
                () -> assertEquals("newCardList", testCardList.getName()),
                () -> assertTrue(testCardList.getArchived()),
                () -> assertEquals(board.getId(), testCardList.getBoardId())
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
