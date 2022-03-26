package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.BoardService;
import spd.trello.services.CardService;

import java.sql.Date;
import java.time.LocalDate;
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
    private BoardService boardService;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        CardList cardList = helper.getNewCardList("create@CardT");

        Card card = new Card();
        card.setCardListId(cardList.getId());
        card.setCreatedBy(cardList.getCreatedBy());
        card.setName("testCardName");
        card.setDescription("description");
        Set<UUID> membersId = boardService.getById(cardList.getBoardId()).getMembersId();
        card.setMembersId(membersId);
        Card testCard = service.save(card);

        assertNotNull(testCard);
        assertAll(
                () -> assertEquals(cardList.getCreatedBy(), testCard.getCreatedBy()),
                () -> assertNull(testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertNull(testCard.getUpdatedDate()),
                () -> assertEquals("testCardName", testCard.getName()),
                () -> assertEquals("description", testCard.getDescription()),
                () -> assertFalse(testCard.getArchived()),
                () -> assertEquals(cardList.getId(), testCard.getCardListId()),
                () -> assertEquals(1, testCard.getMembersId().size())
        );
    }

    @Test
    public void findAll() {
        Card testFirstCard = helper.getNewCard("findAll@CardT");
        Card testSecondCard = helper.getNewCard("2findAll@CardT");

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
        Card card = helper.getNewCard("findById@CardT");

        Card testCard = service.getById(card.getId());
        assertEquals(card, testCard);
    }

    @Test
    public void delete() {
        Card card = helper.getNewCard("delete@CardT");

        assertNotNull(card);
        service.delete(card.getId());
        assertFalse(service.getAll().contains(card));
    }

    @Test
    public void update() {
        Card card = helper.getNewCard("update@CardT");
        Member secondMember = helper.getNewMember("2update@CardT");

        assertNotNull(card);
        card.setUpdatedBy(card.getCreatedBy());
        card.setName("newCard");
        card.setDescription("newDescription");
        card.setArchived(true);
        Set<UUID> membersId = card.getMembersId();
        membersId.add(secondMember.getId());
        card.setMembersId(membersId);
        Card testCard = service.update(card);

        assertAll(
                () -> assertEquals(card.getCreatedBy(), testCard.getCreatedBy()),
                () -> assertEquals(card.getUpdatedBy(), testCard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCard.getUpdatedDate()),
                () -> assertEquals("newCard", testCard.getName()),
                () -> assertEquals("newDescription", testCard.getDescription()),
                () -> assertTrue(testCard.getArchived()),
                () -> assertEquals(card.getCardListId(), testCard.getCardListId()),
                () -> assertEquals(2, testCard.getMembersId().size()),
                () -> assertTrue(testCard.getMembersId().contains(secondMember.getId()))
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