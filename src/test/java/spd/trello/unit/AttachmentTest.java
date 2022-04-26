package spd.trello.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import spd.trello.domain.Attachment;
import spd.trello.domain.Card;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.AttachmentService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        Card card = helper.getNewCard("createWithLink@AT.com");

        Attachment attachment = new Attachment();
        attachment.setName("name");
        attachment.setLink("http://www.example.com/product");
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCreatedDate(LocalDateTime.now().withNano(0));
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
    public void createWithFile() throws JsonProcessingException {
        Card card = helper.getNewCard("createWithFile@AT.com");

        Attachment attachment = new Attachment();
        attachment.setName("name");
        attachment.setType("image");
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCreatedDate(LocalDateTime.now().withNano(0));
        attachment.setCardId(card.getId());

        MultipartFile file = new MockMultipartFile("name", new byte[]{1});
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonAttachment = objectMapper.writeValueAsString(attachment);
        Attachment testAttachment = service.save(jsonAttachment, file);

        assertNotNull(testAttachment);
        assertAll(
                () -> assertEquals(attachment.getCreatedBy(), testAttachment.getCreatedBy()),
                () -> assertNull(testAttachment.getUpdatedBy()),
                () -> assertEquals(attachment.getCreatedDate(), testAttachment.getCreatedDate()),
                () -> assertNull(testAttachment.getUpdatedDate()),
                () -> assertEquals(attachment.getLink(), testAttachment.getLink()),
                () -> assertEquals(card.getId(), testAttachment.getCardId()),
                () -> assertNotNull(testAttachment.getFileId())
        );
    }

    @Test
    public void findAll() {
        Attachment testFirstAttachment = helper.getNewAttachment("findAll@AT.com");
        Attachment testSecondAttachment = helper.getNewAttachment("2findAll@AT.com");

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
        Attachment attachment = helper.getNewAttachment("findById@AT.com");

        Attachment testAttachment = service.getById(attachment.getId());
        assertEquals(attachment, testAttachment);
    }

    @Test
    public void delete() {
        Attachment attachment = helper.getNewAttachment("delete@AT.com");

        service.delete(attachment.getId());
        assertFalse(service.getAll().contains(attachment));
    }

    @Test
    public void update() {
        Attachment attachment = helper.getNewAttachment("update@AT.com");

        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now().withNano(0));
        attachment.setName("newName");
        attachment.setLink("http://www.example.com/product/new");
        Attachment testAttachment = service.update(attachment);

        assertAll(
                () -> assertEquals(attachment.getCreatedBy(), testAttachment.getCreatedBy()),
                () -> assertEquals(attachment.getUpdatedBy(), testAttachment.getUpdatedBy()),
                () -> assertEquals(attachment.getCreatedDate(), testAttachment.getCreatedDate()),
                () -> assertEquals(attachment.getUpdatedDate(), testAttachment.getUpdatedDate()),
                () -> assertEquals(attachment.getName(), testAttachment.getName()),
                () -> assertEquals(attachment.getLink(), testAttachment.getLink()),
                () -> assertEquals(attachment.getCardId(), testAttachment.getCardId())
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
        assertEquals("No class spd.trello.domain.Attachment entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void validationCreate() {
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

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(attachment), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(urlException)),
                () -> assertTrue(ex.getMessage().contains(cardIdException))
        );
    }

    @Test
    public void nullCreatedByFieldCreate() {
        Card card = helper.getNewCard("nullCreatedByFC@AT.com");
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setCreatedDate(LocalDateTime.now().withNano(0));
        attachment.setName("name");
        attachment.setLink("http://www.example.com/product");

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(attachment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldCreate() {
        Card card = helper.getNewCard("nullCreatedDateFC@AT.com");
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setName("name");
        attachment.setLink("http://www.example.com/product");

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(attachment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullNameFieldCreate() {
        Card card = helper.getNewCard("nullNameFieldC@AT.com");
        Attachment attachment = new Attachment();
        attachment.setCardId(card.getId());
        attachment.setCreatedBy(card.getCreatedBy());
        attachment.setCreatedDate(LocalDateTime.now());
        attachment.setLink("http://www.example.com/product");

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(attachment), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
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

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(attachment), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdByUpdateException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(updatedByException)),
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(linkException)),
                () -> assertTrue(ex.getMessage().contains(cardException))
        );
    }

    @Test
    public void nonExistentAttachmentUpdate() {
        Attachment attachment = helper.getNewAttachment("nonExistentCardLU@AT.com");
        attachment.setId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(attachment), "no exception"
        );
        assertEquals("Cannot update non-existent attachment!", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldsUpdate() {
        Attachment attachment = helper.getNewAttachment("nullCreatedByFU@AT.com");
        attachment.setCreatedBy(null);
        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(attachment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldsUpdate() {
        Attachment attachment = helper.getNewAttachment("nullCreatedDFU@AT.com");
        attachment.setCreatedDate(null);
        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(attachment), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedByFieldsUpdate() {
        Attachment attachment = helper.getNewAttachment("nullUpdatedByFieldsUpdate@AT.com");
        attachment.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(attachment), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldsUpdate() {
        Attachment attachment = helper.getNewAttachment("nullUpdatedDateFU@AT.com");
        attachment.setUpdatedBy(attachment.getCreatedBy());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(attachment), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }

    @Test
    public void nullNameUpdate() {
        Attachment attachment = helper.getNewAttachment("nullTextUpdate@AT.com");
        attachment.setUpdatedBy(attachment.getCreatedBy());
        attachment.setUpdatedDate(LocalDateTime.now());
        attachment.setName(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(attachment), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }
}
