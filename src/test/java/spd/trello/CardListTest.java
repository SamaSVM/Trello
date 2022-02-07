package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
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
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test14@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = new CardList();
        cardList.setCreatedBy("test14@mail");
        cardList.setName("testCardList");
        cardList.setBoardId(board.getId());
        CardList testCardList = service.create(cardList);
        assertNotNull(testCardList);
        assertAll(
                () -> assertEquals("test14@mail", testCardList.getCreatedBy()),
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
        CardList cardList = new CardList();
        cardList.setCreatedBy("test15@mail");
        cardList.setBoardId(board.getId());
        cardList.setName("1CardList");
        CardList testFirstCardList = service.create(cardList);
        cardList.setName("2CardList");
        CardList testSecondCardList = service.create(cardList);
        assertNotNull(testFirstCardList);
        assertNotNull(testSecondCardList);
        List<CardList> testCardLists = service.findAll();
        assertAll(
                () -> assertTrue(testCardLists.contains(testFirstCardList)),
                () -> assertTrue(testCardLists.contains(testSecondCardList))
        );
    }

    @Test
    public void createFailure() {
        CardList cardList = new CardList();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(cardList),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("CardList doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("CardList with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test17@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setCreatedBy("test17@mail");
        cardList.setName("testCardList");
        CardList testCardList = service.create(cardList);
        assertNotNull(testCardList);
        UUID id = testCardList.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test18@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setCreatedBy("test18@mail");
        cardList.setName("name");
        CardList updateCardList = service.create(cardList);
        assertNotNull(updateCardList);
        updateCardList.setUpdatedBy("test18@mail");
        updateCardList.setName("newCardList");
        updateCardList.setArchived(true);
        CardList testCardList = service.update(updateCardList);
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
    public void updateFailure() {
        CardList testCardList = new CardList();
        testCardList.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testCardList),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("CardList with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void getAllCardListsForBoard() {
        User user = helper.getNewUser("getAllCardListsForBoard@CLT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = new CardList();
        cardList.setBoardId(board.getId());
        cardList.setCreatedBy("getAllCardListsForBoard@CLT");
        cardList.setName("1board");
        CardList firstBoard = service.create(cardList);
        cardList.setName("2board");
        CardList secondBoard = service.create(cardList);
        assertNotNull(firstBoard);
        assertNotNull(secondBoard);
        List<CardList> boards = service.getAllCardListsForBoard(board.getId());
        assertAll(
                () -> assertTrue(boards.contains(firstBoard)),
                () -> assertTrue(boards.contains(secondBoard)),
                () -> assertEquals(2, boards.size())
        );
    }
}
