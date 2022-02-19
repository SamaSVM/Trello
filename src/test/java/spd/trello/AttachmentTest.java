package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
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
    private Helper helper;

    @Test
    public void create() {
        User user = helper.getNewUser("create@AT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Comment comment = helper.getNewComment(member, card.getId());

        Attachment firstAttachment = new Attachment();
        firstAttachment.setName("1name");
        firstAttachment.setLink("1link");
        firstAttachment.setCreatedBy(user.getEmail());
        firstAttachment.setCardId(card.getId());
        Attachment testFirstAttachment = service.save(firstAttachment);

        Attachment secondAttachment = new Attachment();
        secondAttachment.setName("2name");
        secondAttachment.setLink("2link");
        secondAttachment.setCreatedBy(user.getEmail());
        secondAttachment.setCommentId(comment.getId());
        Attachment testSecondAttachment = service.save(secondAttachment);

        assertNotNull(testFirstAttachment);
        assertNotNull(testSecondAttachment);
        assertAll(
                () -> assertEquals(user.getEmail(), testFirstAttachment.getCreatedBy()),
                () -> assertNull(testFirstAttachment.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testFirstAttachment.getCreatedDate()),
                () -> assertNull(testFirstAttachment.getUpdatedDate()),
                () -> assertEquals("1name", testFirstAttachment.getName()),
                () -> assertEquals("1link", testFirstAttachment.getLink()),
                () -> assertNull(testFirstAttachment.getCommentId()),
                () -> assertEquals(card.getId(), testFirstAttachment.getCardId()),

                () -> assertEquals(user.getEmail(), testSecondAttachment.getCreatedBy()),
                () -> assertNull(testSecondAttachment.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testSecondAttachment.getCreatedDate()),
                () -> assertNull(testSecondAttachment.getUpdatedDate()),
                () -> assertEquals("2name", testSecondAttachment.getName()),
                () -> assertEquals("2link", testSecondAttachment.getLink()),
                () -> assertNull(testSecondAttachment.getCardId()),
                () -> assertEquals(comment.getId(), testSecondAttachment.getCommentId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("findAll@AT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Comment comment = helper.getNewComment(member, card.getId());

        Attachment firstAttachment = new Attachment();
        firstAttachment.setName("1name");
        firstAttachment.setLink("1link");
        firstAttachment.setCreatedBy(user.getEmail());
        firstAttachment.setCardId(card.getId());
        Attachment testFirstAttachment = service.save(firstAttachment);

        Attachment secondAttachment = new Attachment();
        secondAttachment.setName("2name");
        secondAttachment.setLink("2link");
        secondAttachment.setCreatedBy(user.getEmail());
        secondAttachment.setCommentId(comment.getId());
        Attachment testSecondAttachment = service.save(secondAttachment);

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
        User user = helper.getNewUser("findById@AT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Attachment attachment = new Attachment();
        attachment.setName("name");
        attachment.setLink("link");
        attachment.setCreatedBy(user.getEmail());
        attachment.setCardId(card.getId());
        service.save(attachment);

        Attachment testAttachment = service.getById(attachment.getId());
        assertEquals(attachment, testAttachment);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@AT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());

        Attachment attachment = new Attachment();
        attachment.setName("name");
        attachment.setLink("link");
        attachment.setCreatedBy(user.getEmail());
        attachment.setCardId(card.getId());
        Attachment testAttachment = service.save(attachment);

        service.delete(testAttachment.getId());
        assertFalse(service.getAll().contains(testAttachment));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@AT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = helper.getNewBoard(member, workspace.getId());
        CardList cardList = helper.getNewCardList(member, board.getId());
        Card card = helper.getNewCard(member, cardList.getId());
        Comment comment = helper.getNewComment(member, card.getId());

        Attachment attachment = new Attachment();
        attachment.setName("name");
        attachment.setLink("link");
        attachment.setCreatedBy(user.getEmail());
        attachment.setCommentId(comment.getId());
        Attachment updateAttachment = service.save(attachment);

        updateAttachment.setUpdatedBy(user.getEmail());
        updateAttachment.setName("newName");
        updateAttachment.setLink("newLink");

        Attachment testAttachment = service.update(updateAttachment);

        assertAll(
                () -> assertEquals(user.getEmail(), testAttachment.getCreatedBy()),
                () -> assertNull(user.getEmail(), testAttachment.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testAttachment.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testAttachment.getUpdatedDate()),
                () -> assertEquals("newName", testAttachment.getName()),
                () -> assertEquals("newLink", testAttachment.getLink()),
                () -> assertEquals(comment.getId(), testAttachment.getCommentId()),
                () -> assertNull(testAttachment.getCardId())
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
