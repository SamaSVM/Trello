package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.CommentService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CommentTest {
    @Autowired
    private CommentService service;

    @Autowired
    private Helper helper;

    @Test
    public void create() {
        User user = helper.getNewUser("test24@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Comment comment = new Comment();
        comment.setCardId(card.getId());
        comment.setCreatedBy(user.getEmail());
        comment.setText("testText");
        Comment testComment = service.save(comment);

        assertNotNull(testComment);
        assertAll(
                () -> assertEquals(user.getEmail(), testComment.getCreatedBy()),
                () -> assertNull(testComment.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testComment.getCreatedDate()),
                () -> assertNull(testComment.getUpdatedDate()),
                () -> assertEquals("testText", testComment.getText()),
                () -> assertEquals(card.getId(), testComment.getCardId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("test25@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Comment firstComment = new Comment();
        firstComment.setCreatedBy(user.getEmail());
        firstComment.setCardId(card.getId());
        firstComment.setText("1Comment");
        Comment testFirstComment = service.save(firstComment);

        Comment secondComment = new Comment();
        secondComment.setCreatedBy(user.getEmail());
        secondComment.setCardId(card.getId());
        secondComment.setText("2Comment");
        Comment testSecondComment = service.save(secondComment);

        assertNotNull(testFirstComment);
        assertNotNull(testSecondComment);
        List<Comment> testComments = service.getAll();
        assertAll(
                () -> assertTrue(testComments.contains(testFirstComment)),
                () -> assertTrue(testComments.contains(testSecondComment))
        );
    }

    @Test
    public void findById() {
        User user = helper.getNewUser("findById@ComT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Comment comment = new Comment();
        comment.setCreatedBy(user.getEmail());
        comment.setCardId(card.getId());
        comment.setText("Comment");
        service.save(comment);

        Comment testComment = service.getById(comment.getId());
        assertEquals(comment, testComment);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test27@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Comment comment = new Comment();
        comment.setCreatedBy(user.getEmail());
        comment.setCardId(card.getId());
        comment.setText("Comment");
        Comment testComment = service.save(comment);

        assertNotNull(testComment);
        service.delete(testComment.getId());
        assertFalse(service.getAll().contains(testComment));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test28@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Comment comment = new Comment();
        comment.setCreatedBy(user.getEmail());
        comment.setCardId(card.getId());
        comment.setText("Comment");
        Comment com = service.save(comment);

        assertNotNull(com);
        com.setUpdatedBy(user.getEmail());
        com.setText("newText");
        Comment testComment = service.update(com);

        assertAll(
                () -> assertEquals(user.getEmail(), testComment.getCreatedBy()),
                () -> assertEquals(user.getEmail(), testComment.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testComment.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testComment.getUpdatedDate()),
                () -> assertEquals("newText", testComment.getText())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Comment()),
                "no exception"
        );
        assertTrue(ex.getMessage().contains("not-null property references a null or transient value"));
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
        assertEquals("No class spd.trello.domain.Comment entity with id " + id + " exists!", ex.getMessage());
    }
}
