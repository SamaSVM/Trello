package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Attachment;
import spd.trello.domain.Card;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AttachmentIntegrationTest extends AbstractIntegrationTest<Attachment> {
    private final String URL_TEMPLATE = "/attachments";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Card card = helper.getNewCard("createWithLink@AIT.com");
        Attachment attachment = new Attachment();
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCreatedDate(LocalDateTime.now().withNano(0));
        attachment.setName("name");
        attachment.setLink("http://www.example.com/product");
        attachment.setCardId(card.getId());
        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(attachment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(attachment.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(attachment.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(attachment.getLink(), getValue(mvcResult, "$.link")),
                () -> assertEquals(card.getId().toString(), getValue(mvcResult, "$.cardId"))
        );
    }

    @Test
    public void findAll() throws Exception {
        Attachment firstAttachment = helper.getNewAttachment("1findAll@AIT.com");
        Attachment secondAttachment = helper.getNewAttachment("2findAll@AIT.com");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Attachment> testAttachments = helper.getAttachmentsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testAttachments.contains(firstAttachment)),
                () -> assertTrue(testAttachments.contains(secondAttachment))
        );
    }

    @Test
    public void findById() throws Exception {
        Attachment attachment = helper.getNewAttachment("findById@AIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, attachment.getId());
        System.out.println(getValue(mvcResult, "$.createdDate"));
        System.out.println(Date.valueOf(LocalDate.now()));
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(attachment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(attachment.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(attachment.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(attachment.getLink(), getValue(mvcResult, "$.link")),
                () -> assertEquals(attachment.getCardId().toString(), getValue(mvcResult, "$.cardId"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        Attachment attachment = helper.getNewAttachment("deleteById@AIT.com");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, attachment.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Attachment> testAttachments = helper.getAttachmentsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testAttachments.contains(attachment))
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
        Attachment attachment = helper.getNewAttachment("update@AIT.com");
        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now().withNano(0));
        attachment.setName("new name");
        attachment.setLink("http://www.example.com/product/new");

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(attachment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(attachment.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(attachment.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(attachment.getUpdatedDate().toString(), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(attachment.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(attachment.getLink(), getValue(mvcResult, "$.link")),
                () -> assertEquals(attachment.getCardId().toString(), getValue(mvcResult, "$.cardId"))
        );
    }

    @Test
    public void validationCreate() throws Exception {
        Attachment attachment = new Attachment();
        attachment.setName("n");
        attachment.setCreatedBy("c");
        attachment.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        attachment.setLink("link");
        attachment.setCardId(UUID.randomUUID());

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String urlException = "The link must be in the form of a URL. \n";
        String cardIdException = "The cardId field must belong to a card. \n";

        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(urlException)),
                () -> assertTrue(exceptionMessage.contains(cardIdException))
        );
    }

    @Test
    public void nullCreatedByFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullCreatedByFC@AT.com");
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setCreatedDate(LocalDateTime.now().withNano(0));
        attachment.setName("name");
        attachment.setLink("http://www.example.com/product");

        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullCreatedDateFC@AT.com");
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setName("name");
        attachment.setLink("http://www.example.com/product");

        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameFieldCreate() throws Exception {
        Card card = helper.getNewCard("nullNameFieldC@AT.com");
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCreatedDate(LocalDateTime.now());
        attachment.setLink("http://www.example.com/product");

        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void validationUpdate() throws Exception {
        Attachment attachment = helper.getNewAttachment("validationUpdate@AT.com");
        attachment.setCreatedBy("c");
        attachment.setCreatedDate(LocalDateTime.now());
        attachment.setUpdatedBy("u");
        attachment.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        attachment.setName("n");
        attachment.setLink("link");
        attachment.setCardId(UUID.randomUUID());


        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdByUpdateException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String updatedDateException = "The updatedDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long.";
        String linkException = "The link must be in the form of a URL.";
        String cardException = "Attachment cannot be transferred to another card. \n";

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdByUpdateException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(updatedByException)),
                () -> assertTrue(exceptionMessage.contains(updatedDateException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(linkException)),
                () -> assertTrue(exceptionMessage.contains(cardException))
        );
    }

    @Test
    public void nonExistentAttachmentUpdate() throws Exception {
        Attachment attachment = helper.getNewAttachment("nonExistentCardLU@AT.com");
        attachment.setId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent attachment!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldsUpdate() throws Exception {
        Attachment attachment = helper.getNewAttachment("nullCreatedByFU@AT.com");
        attachment.setCreatedBy(null);
        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldsUpdate() throws Exception {
        Attachment attachment = helper.getNewAttachment("nullCreatedDFU@AT.com");
        attachment.setCreatedDate(null);
        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByFieldsUpdate() throws Exception {
        Attachment attachment = helper.getNewAttachment("nullUpdatedByFieldsUpdate@AT.com");
        attachment.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() throws Exception {
        Attachment attachment = helper.getNewAttachment("nullUpdatedDateFU@AT.com");
        attachment.setUpdatedBy(attachment.getCreatedBy());

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameUpdate() throws Exception {
        Attachment attachment = helper.getNewAttachment("nullTextUpdate@AT.com");
        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now());
        attachment.setName(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }
}
