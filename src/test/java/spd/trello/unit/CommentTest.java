package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Card;
import spd.trello.domain.Comment;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.CommentService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentTest {
    @Autowired
    private CommentService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        Card card = helper.getNewCard("create@CT");

        Comment comment = new Comment();
        comment.setCardId(card.getId());
        comment.setCreatedBy(card.getCreatedBy());
        comment.setText("testText");
        Comment testComment = service.save(comment);

        assertNotNull(testComment);
        assertAll(
                () -> assertEquals(comment.getCreatedBy(), testComment.getCreatedBy()),
                () -> assertNull(testComment.getUpdatedBy()),
                () -> assertTrue(testComment.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(testComment.getUpdatedDate()),
                () -> assertEquals("testText", testComment.getText()),
                () -> assertEquals(card.getId(), testComment.getCardId())
        );
    }

    @Test
    public void findAll() {
        Comment testFirstComment = helper.getNewComment("findAll@CT");
        Comment testSecondComment = helper.getNewComment("2findAll@CT");

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
        Comment comment = helper.getNewComment("findById@CT");

        Comment testComment = service.getById(comment.getId());
        assertEquals(comment, testComment);
    }

    @Test
    public void delete() {
        Comment comment = helper.getNewComment("delete@CT");

        assertNotNull(comment);
        service.delete(comment.getId());
        assertFalse(service.getAll().contains(comment));
    }

    @Test
    public void update() {
        Comment comment = helper.getNewComment("update@CT");

        assertNotNull(comment);
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setText("newText");
        Comment testComment = service.update(comment);

        assertAll(
                () -> assertEquals(comment.getCreatedBy(), testComment.getCreatedBy()),
                () -> assertEquals(comment.getUpdatedBy(), testComment.getUpdatedBy()),
                () -> assertTrue(testComment.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertTrue(testComment.getUpdatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals(comment.getText(), testComment.getText())
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
