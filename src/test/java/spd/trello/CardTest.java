package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.CardService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public void create() {
        User user = helper.getNewUser("test19@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());

        Card card = new Card();
        card.setCardListId(cardList.getId());
        card.setCreatedBy(user.getEmail());
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        card.setMembersIds(membersIds);
        Card testCard = service.save(card);

        assertNotNull(testCard);
        assertAll(
                () -> assertEquals(user.getEmail(), testCard.getCreatedBy()),
                () -> assertNull(testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertNull(testCard.getUpdatedDate()),
                () -> assertEquals("testCardName", testCard.getName()),
                () -> assertEquals("description", testCard.getDescription()),
                () -> assertFalse(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId()),
                () -> assertTrue(testCard.getMembersIds().contains(member.getId()))
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("test20@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());

        Card firstCard = new Card();
        firstCard.setCreatedBy(user.getEmail());
        firstCard.setCardListId(cardList.getId());
        firstCard.setName("1Card");
        firstCard.setDescription("1description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        firstCard.setMembersIds(membersIds);
        Card testFirstCard = service.save(firstCard);

        Card secondCard = new Card();
        secondCard.setCardListId(cardList.getId());
        secondCard.setCreatedBy(user.getEmail());
        secondCard.setName("2Card");
        secondCard.setDescription("2description");
        secondCard.setMembersIds(membersIds);
        Card testSecondCard = service.save(secondCard);

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
        User user = helper.getNewUser("findById@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());

        Card card = new Card();
        card.setCreatedBy(user.getEmail());
        card.setCardListId(cardList.getId());
        card.setName("Card");
        card.setDescription("description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        card.setMembersIds(membersIds);
        service.save(card);

        Card testCard = service.getById(card.getId());
        assertEquals(card, testCard);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test22@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());

        Card card = new Card();
        card.setCreatedBy(user.getEmail());
        card.setCardListId(cardList.getId());
        card.setName("Card");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        card.setMembersIds(membersIds);
        Card testCard = service.save(card);

        assertNotNull(testCard);
        service.delete(testCard.getId());
        assertFalse(service.getAll().contains(testCard));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test23@mail");
        Member firstMember = helper.getNewMember(user);
        Member secondMember = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board board = helper.getNewBoard(firstMember, workspace.getId());
        CardList cardList = helper.getNewCardList(firstMember, board.getId());

        Card card = new Card();
        card.setCreatedBy(user.getEmail());
        card.setCardListId(cardList.getId());
        card.setName("Card");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(firstMember.getId());
        card.setMembersIds(membersIds);
        Card updateCard = service.save(card);

        assertNotNull(updateCard);
        updateCard.setUpdatedBy(user.getEmail());
        updateCard.setName("newCard");
        updateCard.setDescription("newDescription");
        updateCard.setArchived(true);
        membersIds.add(secondMember.getId());
        updateCard.setMembersIds(membersIds);
        Card testCard = service.update(updateCard);

        assertAll(
                () -> assertEquals(user.getEmail(), testCard.getCreatedBy()),
                () -> assertEquals(user.getEmail(), testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getUpdatedDate()),
                () -> assertEquals("newCard", testCard.getName()),
                () -> assertEquals("newDescription", testCard.getDescription()),
                () -> assertTrue(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId()),
                () -> assertTrue(testCard.getMembersIds().contains(firstMember.getId())),
                () -> assertTrue(testCard.getMembersIds().contains(secondMember.getId()))
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Card()),
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
        assertEquals("No class spd.trello.domain.Card entity with id " + id + " exists!", ex.getMessage());
    }
}
