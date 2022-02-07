package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
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
    public void successCreate() {
        User user = helper.getNewUser("test24@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Comment comment = new Comment();
        comment.setCardId(card.getId());
        comment.setCreatedBy("test24@mail");
        comment.setText("testText");
        Comment testComment = service.create(comment);
        assertNotNull(testComment);
        assertAll(
                () -> assertEquals("test24@mail", testComment.getCreatedBy()),
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
        Comment comment = new Comment();
        comment.setCreatedBy("test25@mail");
        comment.setCardId(card.getId());
        comment.setText("1Comment");
        Comment testFirstComment = service.create(comment);
        comment.setText("2Comment");
        Comment testSecondComment = service.create(comment);
        assertNotNull(testFirstComment);
        assertNotNull(testSecondComment);
        List<Comment> testComments = service.findAll();
        assertAll(
                () -> assertTrue(testComments.contains(testFirstComment)),
                () -> assertTrue(testComments.contains(testSecondComment))
        );
    }

    @Test
    public void createFailure() {
        Comment comment = new Comment();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(comment),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Comment doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Comment with ID: " + uuid + " doesn't exists", ex.getMessage());
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
        comment.setCardId(card.getId());
        comment.setCreatedBy("test27@mail");
        comment.setText("Text");
        Comment testComment = service.create(comment);
        assertNotNull(testComment);
        UUID id = testComment.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test28@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Comment updateComment = new Comment();
        updateComment.setCardId(card.getId());
        updateComment.setCreatedBy("test28@mail");
        updateComment.setText("text");
        Comment comment = service.create(updateComment);
        assertNotNull(comment);
        comment.setUpdatedBy("test28@mail");
        comment.setText("newText");
        Comment testComment = service.update(comment);
        assertAll(
                () -> assertEquals("test28@mail", testComment.getCreatedBy()),
                () -> assertEquals("test28@mail", testComment.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testComment.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testComment.getUpdatedDate()),
                () -> assertEquals("newText", testComment.getText())
        );
    }

    @Test
    public void updateFailure() {
        Comment testComment = new Comment();
        testComment.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testComment),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Comment with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void getAllCommentsForCard() {
        User user = helper.getNewUser("getAllCommentsForCard@CT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Comment firstComment = helper.getNewComment(member, card.getId());
        Comment secondComment = helper.getNewComment(member, card.getId());
        assertNotNull(card);
        List<Comment> comments = service.getAllCommentsForCard(card.getId());
        assertAll(
                () -> assertTrue(comments.contains(firstComment)),
                () -> assertTrue(comments.contains(secondComment)),
                () -> assertEquals(2, comments.size())
        );
    }
}
