package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Card;
import spd.trello.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentIntegrationTest extends AbstractIntegrationTest<Comment> {
    private final String URL_TEMPLATE = "/comments";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Card card = helper.getNewCard("create@ComIT.com");
        Comment comment = new Comment();
        comment.setCreatedBy(card.getCreatedBy());
        comment.setCreatedDate(LocalDateTime.now().withNano(0));
        comment.setCardId(card.getId());
        comment.setText("text");

        MvcResult mvcResult = super.create(URL_TEMPLATE, comment);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(comment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(comment.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(comment.getText(), getValue(mvcResult, "$.text")),
                () -> assertEquals(card.getId().toString(), getValue(mvcResult, "$.cardId"))
        );
    }

    @Test
    public void findAll() throws Exception {
        Comment firstComment = helper.getNewComment("1findAll@ComIT.com");
        Comment secondComment = helper.getNewComment("2findAll@ComIT.com");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Comment> testCardLists = helper.getCommentsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testCardLists.contains(firstComment)),
                () -> assertTrue(testCardLists.contains(secondComment))
        );
    }

    @Test
    public void findById() throws Exception {
        Comment comment = helper.getNewComment("findById@ComIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, comment.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(comment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(comment.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(comment.getText(), getValue(mvcResult, "$.text")),
                () -> assertEquals(comment.getCardId().toString(), getValue(mvcResult, "$.cardId"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        Comment comment = helper.getNewComment("deleteById@ComIT.com");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, comment.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Comment> testComments = helper.getCommentsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testComments.contains(comment))
        );
    }

    @Test
    public void deleteByIdFailure() throws Exception {
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, UUID.randomUUID());

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus())
        );
    }

    @Test
    public void update() throws Exception {
        Comment comment = helper.getNewComment("update@ComIT.com");
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now().withNano(0));
        comment.setText("new text");

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(comment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(comment.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(comment.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(comment.getUpdatedDate().toString(), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(comment.getText(), getValue(mvcResult, "$.text")),
                () -> assertEquals(comment.getCardId().toString(), getValue(mvcResult, "$.cardId"))
        );
    }

    @Test
    public void validationCreate() throws Exception {
        Comment comment = new Comment();
        comment.setCreatedBy("c");
        comment.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        comment.setText("t");
        comment.setCardId(UUID.randomUUID());

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String textException = "The text field must be between 2 and 1000 characters long. \n";
        String cardIdException = "The cardId field must belong to a card. \n";

        MvcResult mvcResult = super.create(URL_TEMPLATE, comment);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(textException)),
                () -> assertTrue(exceptionMessage.contains(cardIdException))
        );
    }

    @Test
    public void nullCreatedByFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullCreatedByFC@ComIT.com");
        Comment comment = new Comment();
        comment.setCreatedDate(LocalDateTime.now().withNano(0));
        comment.setText("text");
        comment.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullCreatedDateFC@ComIT.com");
        Comment comment = new Comment();
        comment.setCreatedBy(card.getCreatedBy());
        comment.setText("text");
        comment.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullTextFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullTextFC@ComIT.com");
        Comment comment = new Comment();
        comment.setCreatedBy(card.getCreatedBy());
        comment.setCreatedDate(LocalDateTime.now());
        comment.setCardId(card.getId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The text field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCardIdCreate() throws Exception {
        Comment comment = new Comment();
        comment.setCreatedBy("createBy");
        comment.setCreatedDate(LocalDateTime.now());
        comment.setText("text");

        MvcResult mvcResult = super.create(URL_TEMPLATE, comment);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Card cannot be null!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void validationUpdate() throws Exception {
        Comment comment = helper.getNewComment("validationUpdate@ComIT.com");
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

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdByUpdateException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(updatedByException)),
                () -> assertTrue(exceptionMessage.contains(updatedDateException)),
                () -> assertTrue(exceptionMessage.contains(textException)),
                () -> assertTrue(exceptionMessage.contains(commentException))
        );
    }

    @Test
    public void nonExistentCommentUpdate() throws Exception {
        Comment comment = helper.getNewComment("nonExistentCardLU@ComIT.com");
        comment.setId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent comment!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldsUpdate() throws Exception {
        Comment comment = helper.getNewComment("nullCreatedByFU@ComIT.com");
        comment.setCreatedBy(null);
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldsUpdate() throws Exception {
        Comment comment = helper.getNewComment("nullCreatedDFU@ComIT.com");
        comment.setCreatedDate(null);
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByFieldsUpdate() throws Exception {
        Comment comment = helper.getNewComment("nullUpdatedByFieldsUpdate@ComIT.com");
        comment.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() throws Exception {
        Comment comment = helper.getNewComment("nullUpdatedDateFU@ComIT.com");
        comment.setUpdatedBy(comment.getCreatedBy());

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullTextUpdate() throws Exception {
        Comment comment = helper.getNewComment("nullTextUpdate@ComIT.com");
        comment.setUpdatedBy(comment.getCreatedBy());
        comment.setUpdatedDate(LocalDateTime.now());
        comment.setText(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, comment.getId(), comment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The text field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }
}
