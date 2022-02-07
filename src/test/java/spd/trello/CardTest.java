package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.services.CardService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CardTest {
    @Autowired
    private CardService service;

    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test19@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = new Card();
        card.setCardListId(cardList.getId());
        card.setCreatedBy("test19@mail");
        card.setName("testCardName");
        card.setDescription("description");
        Card testCard = service.create(card);
        assertNotNull(testCard);
        assertAll(
                () -> assertEquals("test19@mail", testCard.getCreatedBy()),
                () -> assertNull(testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertNull(testCard.getUpdatedDate()),
                () -> assertEquals("testCardName", testCard.getName()),
                () -> assertEquals("description", testCard.getDescription()),
                () -> assertFalse(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("test20@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = new Card();
        card.setCreatedBy("test20@mail");
        card.setCardListId(cardList.getId());
        card.setName("1Card");
        card.setDescription("1description");
        Card testFirstCard = service.create(card);
        card.setName("2Card");
        card.setDescription("2description");
        Card testSecondCard = service.create(card);
        assertNotNull(testFirstCard);
        assertNotNull(testSecondCard);
        List<Card> testCard = service.findAll();
        assertAll(
                () -> assertTrue(testCard.contains(testFirstCard)),
                () -> assertTrue(testCard.contains(testSecondCard))
        );
    }

    @Test
    public void createFailure() {
        Card card = new Card();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(card),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Card doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Card with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test22@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = new Card();
        card.setCardListId(cardList.getId());
        card.setCreatedBy("test22@mail");
        card.setName("testCard");
        card.setDescription("description");
        Card testCard = service.create(card);
        assertNotNull(testCard);
        UUID id = testCard.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test23@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = new Card();
        card.setCardListId(cardList.getId());
        card.setCreatedBy("test23@mail");
        card.setName("name");
        card.setDescription("description");
        Card updateCard = service.create(card);
        assertNotNull(updateCard);
        updateCard.setUpdatedBy("test23@mail");
        updateCard.setName("newCard");
        updateCard.setDescription("newDescription");
        updateCard.setArchived(true);
        Card testCard = service.update(updateCard);
        assertAll(
                () -> assertEquals("test23@mail", testCard.getCreatedBy()),
                () -> assertEquals("test23@mail", testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getUpdatedDate()),
                () -> assertEquals("newCard", testCard.getName()),
                () -> assertEquals("newDescription", testCard.getDescription()),
                () -> assertTrue(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId())
        );
    }

    @Test
    public void updateFailure() {
        Card testCard = new Card();
        testCard.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testCard),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Card with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void addAndDeleteSecondMember() {
        User firstUser = helper.getNewUser("addAndDeleteSecondMember1@CT");
        User secondUser = helper.getNewUser("addAndDeleteSecondMember2@CT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board board = helper.getNewBoard(firstMember, workspace.getId());
        CardList cardList = helper.getNewCardList(firstMember, board.getId());
        Card card = new Card();
        card.setCardListId(cardList.getId());
        card.setCreatedBy("addAndDeleteSecondMember1@CT");
        card.setName("testCard");
        card.setDescription("testDescription");
        Card testCard = service.create(card);
        assertNotNull(testCard);
        assertAll(
                () -> assertTrue(service.addMember(secondMember.getId(), testCard.getId())),
                () -> assertTrue(service.deleteMember(secondMember.getId(), testCard.getId()))
        );
    }

    @Test
    public void getAllCardsForCardList() {
        User firstUser = helper.getNewUser("getAllCardsForCardList@CLT");
        Member firstMember = helper.getNewMember(firstUser);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board board = helper.getNewBoard(firstMember, workspace.getId());
        CardList testCardList = helper.getNewCardList(firstMember, board.getId());
        Card firstCard = helper.getNewCard(firstMember, testCardList.getId());
        Card secondCard = helper.getNewCard(firstMember, testCardList.getId());
        assertNotNull(testCardList);
        List<Card> cards = service.getAllCardsForCardList(testCardList.getId());
        assertAll(
                () -> assertTrue(cards.contains(firstCard)),
                () -> assertTrue(cards.contains(secondCard)),
                () -> assertEquals(2, cards.size())
        );
    }
}
