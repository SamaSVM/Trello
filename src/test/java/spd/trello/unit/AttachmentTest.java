package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Attachment;
import spd.trello.domain.Card;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.AttachmentService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AttachmentTest {
    @Autowired
    private AttachmentService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void createWithLink() {
        Card card = helper.getNewCard("createWithLink@AT");

        Attachment attachment = new Attachment();
        attachment.setName("name");
        attachment.setLink("link");
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCardId(card.getId());
        Attachment testAttachment = service.save(attachment);

        assertNotNull(testAttachment);
        assertAll(
                () -> assertEquals(attachment.getCreatedBy(), testAttachment.getCreatedBy()),
                () -> assertNull(testAttachment.getUpdatedBy()),
                () -> assertTrue(testAttachment.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(testAttachment.getUpdatedDate()),
                () -> assertEquals(attachment.getName(), testAttachment.getName()),
                () -> assertEquals(attachment.getLink(), testAttachment.getLink()),
                () -> assertEquals(card.getId(), testAttachment.getCardId())
        );
    }

    @Test
    public void createWithFile() {
        Card card = helper.getNewCard("createWithFile@AT");

        FileDB fileDB = new FileDB();
        fileDB.setData(new byte[]{1});

        Attachment attachment = new Attachment();
        attachment.setName("name");
        attachment.setType("image");
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCardId(card.getId());
        attachment.setFileDB(fileDB);
        Attachment testAttachment = service.save(attachment);

        assertNotNull(testAttachment);
        assertAll(
                () -> assertEquals(attachment.getCreatedBy(), testAttachment.getCreatedBy()),
                () -> assertNull(testAttachment.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testAttachment.getCreatedDate()),
                () -> assertNull(testAttachment.getUpdatedDate()),
                () -> assertEquals(attachment.getName(), testAttachment.getName()),
                () -> assertEquals(attachment.getLink(), testAttachment.getLink()),
                () -> assertEquals(card.getId(), testAttachment.getCardId()),
                () -> assertEquals(fileDB, attachment.getFileDB())
        );
    }

    @Test
    public void findAll() {
        Attachment testFirstAttachment = helper.getNewAttachment("findAll@AT");
        Attachment testSecondAttachment = helper.getNewAttachment("2findAll@AT");

        assertNotNull(testFirstAttachment);
        assertNotNull(testSecondAttachment);
        List<Attachment> testAttachments = service.getAll();
        assertAll(
                () -> assertTrue(testAttachments.contains(testFirstAttachment)),
                () -> assertTrue(testAttachments.contains(testSecondAttachment))
        );
    }

    @Test
    public void findById() {
        Attachment attachment = helper.getNewAttachment("findById@AT");

        Attachment testAttachment = service.getById(attachment.getId());
        assertEquals(attachment, testAttachment);
    }

    @Test
    public void delete() {
        Attachment attachment = helper.getNewAttachment("delete@AT");

        service.delete(attachment.getId());
        assertFalse(service.getAll().contains(attachment));
    }

    @Test
    public void update() {
        Attachment attachment = helper.getNewAttachment("update@AT");

        FileDB fileDB = new FileDB();
        fileDB.setData(new byte[]{1});

        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setName("newName");
        attachment.setFileDB(fileDB);

        Attachment testAttachment = service.update(attachment);

        assertAll(
                () -> assertEquals(attachment.getCreatedBy(), testAttachment.getCreatedBy()),
                () -> assertEquals(attachment.getUpdatedBy(), testAttachment.getUpdatedBy()),
                () -> assertTrue(testAttachment.getCreatedDate().toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertTrue(testAttachment.getUpdatedDate().toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals(attachment.getName(), testAttachment.getName()),
                () -> assertEquals(attachment.getLink(), testAttachment.getLink()),
                () -> assertEquals(attachment.getCardId(), testAttachment.getCardId()),
                () -> assertEquals(fileDB, attachment.getFileDB())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Attachment()),
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
        assertEquals("No class spd.trello.domain.Attachment entity with id " + id + " exists!", ex.getMessage());
    }
}
