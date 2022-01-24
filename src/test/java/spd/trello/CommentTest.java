package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.CommentRepository;
import spd.trello.services.CommentService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static spd.trello.Helper.*;
import static spd.trello.Helper.getNewCardList;

public class CommentTest extends BaseTest {
    private final CommentService service = context.getBean(CommentService.class);

    @Test
    public void successCreate() {
        User user = getNewUser("test24@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Comment testComment = service.create(member, card.getId(), "testText");
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
        User user = getNewUser("test25@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Comment testFirstComment = service.create(member, card.getId(), "1Comment");
        Comment testSecondComment = service.create(member, card.getId(), "2Comment");
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
        User user = getNewUser("test26@mail");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Text"),
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
        User user = getNewUser("test27@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Comment testComment = service.create(member, card.getId(), "Text");
        assertNotNull(testComment);
        UUID id = testComment.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = getNewUser("test28@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Comment comment = service.create(member, card.getId(), "text");
        assertNotNull(comment);
        comment.setText("newText");
        Comment testComment = service.update(member, comment);
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
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Comment testComment = new Comment();
        testComment.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testComment),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Comment with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
