package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Attachment;
import spd.trello.domain.Card;
import spd.trello.domain.FileDB;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AttachmentIntegrationTest extends AbstractIntegrationTest<Attachment> {
    private final String URL_TEMPLATE = "/attachments";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void createWithLink() throws Exception {
        Card card = helper.getNewCard("createWithLink@AttachmentIntegrationTest");
        Attachment attachment = new Attachment();
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setName("name");
        attachment.setLink("link");
        attachment.setCardId(card.getId());
        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(attachment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(attachment.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(attachment.getLink(), getValue(mvcResult, "$.link")),
                () -> assertEquals(card.getId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertNull(attachment.getFileDB())
        );
    }

    @Test
    public void createWithFile() throws Exception {
        Card card = helper.getNewCard("createWithFile@AttachmentIntegrationTest");

        FileDB fileDB = new FileDB();
        fileDB.setName("name");
        fileDB.setType("image");
        fileDB.setData(new byte[]{1});

        Attachment attachment = new Attachment();
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setName("name");
        attachment.setCardId(card.getId());
        attachment.setFileDB(fileDB);
        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(attachment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(attachment.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(attachment.getLink(), getValue(mvcResult, "$.link")),
                () -> assertEquals(card.getId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertEquals(fileDB.getId().toString(), getValue(mvcResult, "$.fileDB.id")),
                () -> assertEquals(fileDB.getName(), getValue(mvcResult, "$.fileDB.name")),
                () -> assertEquals(fileDB.getType(), getValue(mvcResult, "$.fileDB.type")),
                () -> assertNotNull(getValue(mvcResult, "$.fileDB.data"))
        );
    }

    @Test
    public void createFailure() throws Exception {
        Attachment attachment = new Attachment();
        MvcResult mvcResult = super.create(URL_TEMPLATE, attachment);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        Attachment firstAttachment = helper.getNewAttachment("1findAll@AttachmentIntegrationTest");
        Attachment secondAttachment = helper.getNewAttachment("2findAll@AttachmentIntegrationTest");
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
        Attachment attachment = helper.getNewAttachment("findById@AttachmentIntegrationTest");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, attachment.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(attachment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
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
        Attachment attachment = helper.getNewAttachment("deleteById@AttachmentIntegrationTest");
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
        Attachment attachment = helper.getNewAttachment("update@AttachmentIntegrationTest");
        attachment.setUpdatedBy(attachment.getCreatedBy());
        FileDB fileDB = new FileDB();
        fileDB.setName("name");
        fileDB.setType("image");
        fileDB.setData(new byte[]{1});
        attachment.setName("new named");
        attachment.setFileDB(fileDB);

        MvcResult mvcResult = super.update(URL_TEMPLATE, attachment.getId(), attachment);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(attachment.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(attachment.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(attachment.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(attachment.getLink(), getValue(mvcResult, "$.link")),
                () -> assertEquals(attachment.getCardId().toString(), getValue(mvcResult, "$.cardId")),
                () -> assertEquals(fileDB.getId().toString(), getValue(mvcResult, "$.fileDB.id")),
                () -> assertEquals(fileDB.getName(), getValue(mvcResult, "$.fileDB.name")),
                () -> assertEquals(fileDB.getType(), getValue(mvcResult, "$.fileDB.type")),
                () -> assertNotNull(getValue(mvcResult, "$.fileDB.data"))
        );
    }

    @Test
    public void updateFailure() throws Exception {
        Attachment firstAttachment = helper.getNewAttachment("1updateFailure@AttachmentIntegrationTest");

        Attachment secondAttachment = new Attachment();
        secondAttachment.setId(firstAttachment.getId());

        MvcResult firstMvcResult = super.update(URL_TEMPLATE, UUID.randomUUID(), firstAttachment);
        MvcResult secondMvcResult = super.update(URL_TEMPLATE, secondAttachment.getId(), secondAttachment);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), secondMvcResult.getResponse().getStatus())
        );
    }
}
