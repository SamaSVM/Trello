package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Card;
import spd.trello.domain.Comment;
import spd.trello.exception.BadRequestException;
import spd.trello.exception.ResourceNotFoundException;
import spd.trello.services.CommentService;

import java.time.LocalDateTime;
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
        Card card = helper.getNewCard("create@com.com");

        Comment comment = new Comment();
        comment.setCardId(card.getId());
        comment.setCreatedBy(card.getCreatedBy());
        comment.setCreatedDate(LocalDateTime.now().withNano(0));
        comment.setText("testText");
        Comment testComment = service.save(comment);

        assertNotNull(testComment);
        assertAll(
                () -> assertEquals(comment.getCreatedBy(), testComment.getCreatedBy()),
                () -> assertNull(testComment.getUpdatedBy()),
                () -> assertEquals(comment.getCreatedDate(), testComment.getCreatedDate()),
                () -> assertNull(testComment.getUpdatedDate()),
                () -> assertEquals(comment.getText(), testComment.getText()),
                () -> assertEquals(card.getId(), testComment.getCardId())
        );
    }

    @Test
    public void findAll() {
        Comment testFirstComment = helper.getNewComment("findAll@com.com");
        Comment testSecondComment = helper.getNewComment("2findAll@com.com");

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
        Comment comment = helper.getNewComment("findById@com.com");

        Comment testComment = service.getById(comment.getId());
        assertEquals(comment, testComment);
    }

    @Test
    public void delete() {
        Comment comment = helper.getNewComment("delete@com.com");

        assertNotNull(comment);
        service.delete(comment.getId());
        assertFalse(service.getAll().contains(comment));
    }

    @Test
    public void update() {
        Comment comment = helper.getNewComment("update@com.com");

        assertNotNull(comment);
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now().withNano(0));
        comment.setText("newText");
        Comment testComment = service.update(comment);

        assertAll(
                () -> assertEquals(comment.getCreatedBy(), testComment.getCreatedBy()),
                () -> assertEquals(comment.getUpdatedBy(), testComment.getUpdatedBy()),
                () -> assertEquals(comment.getCreatedDate(), testComment.getCreatedDate()),
                () -> assertEquals(comment.getUpdatedDate(), testComment.getUpdatedDate()),
                () -> assertEquals(comment.getText(), testComment.getText())
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
        assertEquals("No class spd.trello.domain.Comment entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void validationCreate() {
        Comment comment = new Comment();
        comment.setCreatedBy("c");
        comment.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        comment.setText("t");
        comment.setCardId(UUID.randomUUID());

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String textException = "The text field must be between 2 and 1000 characters long. \n";
        String cardIdException = "The cardId field must belong to a card. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(comment), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(textException)),
                () -> assertTrue(ex.getMessage().contains(cardIdException))
        );
    }

    @Test
    public void nullCreatedByFieldCreate() {
        Card card = helper.getNewCard("nullCreatedByFC@com.com");
        Comment comment = new Comment();
        comment.setCreatedDate(LocalDateTime.now().withNano(0));
        comment.setText("text");
        comment.setCardId(card.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(comment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldCreate() {
        Card card = helper.getNewCard("nullCreatedDateFC@com.com");
        Comment comment = new Comment();
        comment.setCreatedBy(card.getCreatedBy());
        comment.setText("text");
        comment.setCardId(card.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(comment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullTextFieldCreate() {
        Card card = helper.getNewCard("nullTextFC@com.com");
        Comment comment = new Comment();
        comment.setCreatedBy(card.getCreatedBy());
        comment.setCreatedDate(LocalDateTime.now());
        comment.setCardId(card.getId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(comment), "no exception"
        );
        assertEquals("The text field must be filled.", ex.getMessage());
    }

    @Test
    public void nullCardIdCreate() {
        Comment comment = new Comment();
        comment.setCreatedBy("createBy");
        comment.setCreatedDate(LocalDateTime.now());
        comment.setText("text");

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(comment), "no exception"
        );
        assertEquals("Card cannot be null!", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
        Comment comment = helper.getNewComment("validationUpdate@com.com");
        comment.setCreatedBy("c");
        comment.setCreatedDate(LocalDateTime.now());
        comment.setUpdatedBy("u");
        comment.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        comment.setText("t");
        comment.setCardId(UUID.randomUUID());

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdByUpdateException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String updatedDateException = "The updatedDate should not be past or future. \n";
        String textException = "The text field must be between 2 and 1000 characters long. \n";
        String commentException = "Comment cannot be transferred to another card. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(comment), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdByUpdateException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(updatedByException)),
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(textException)),
                () -> assertTrue(ex.getMessage().contains(commentException))
        );
    }

    @Test
    public void nonExistentCommentUpdate() {
        Comment comment = helper.getNewComment("nonExistentCardLU@com.com");
        comment.setId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(comment), "no exception"
        );
        assertEquals("Cannot update non-existent comment!", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldsUpdate() {
        Comment comment = helper.getNewComment("nullCreatedByFU@com.com");
        comment.setCreatedBy(null);
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(comment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldsUpdate() {
        Comment comment = helper.getNewComment("nullCreatedDFU@com.com");
        comment.setCreatedDate(null);
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(comment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedByFieldsUpdate() {
        Comment comment = helper.getNewComment("nullUpdatedByFieldsUpdate@com.com");
        comment.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(comment), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() {
        Comment comment = helper.getNewComment("nullUpdatedDateFU@com.com");
        comment.setUpdatedBy(comment.getCreatedBy());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(comment), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }

    @Test
    public void nullTextUpdate() {
        Comment comment = helper.getNewComment("nullTextUpdate@com.com");
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now());
        comment.setText(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(comment), "no exception"
        );
        assertEquals("The text field must be filled.", ex.getMessage());
    }
}
