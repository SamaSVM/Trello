package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.BoardRepository;
import spd.trello.services.BoardService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static spd.trello.Helper.*;
import static spd.trello.Helper.deleteUser;

public class BoardTest extends BaseTest{
    private final BoardService service;

    public BoardTest() {
        service = new BoardService(new BoardRepository(dataSource));
    }

    @Test
    public void successCreate() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board testBoard = service.create(member, workspace.getId(), "testBoard", "testDescription");
        assertNotNull(testBoard);
        assertAll(
                () -> assertEquals("test@mail", testBoard.getCreatedBy()),
                () -> assertNull(testBoard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getCreatedDate()),
                () -> assertNull(testBoard.getUpdatedDate()),
                () -> assertEquals("testBoard", testBoard.getName()),
                () -> assertEquals("testDescription", testBoard.getDescription()),
                () -> assertTrue(testBoard.getMembers().contains(member)),
                () -> assertEquals(BoardVisibility.PRIVATE, testBoard.getVisibility()),
                () -> assertFalse(testBoard.getFavourite()),
                () -> assertFalse(testBoard.getArchived()),
                () -> assertEquals(workspace.getId(), testBoard.getWorkspaceId())
                );
        service.delete(testBoard.getId());
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void testFindAll() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board testFirstBoard = service.create(member, workspace.getId(), "1Board", "1Description");
        Board testSecondBoard = service.create(member, workspace.getId(), "2Board", "2Description");
        assertNotNull(testFirstBoard);
        assertNotNull(testSecondBoard);
        List<Board> testBoard = service.findAll();
        assertAll(
                () -> assertTrue(testBoard.contains(testFirstBoard)),
                () -> assertTrue(testBoard.contains(testSecondBoard))
        );
        for (Board board : testBoard) {
            service.delete(board.getId());
        }
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void createFailure() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Name", "Description"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Board doesn't creates", ex.getMessage());
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void testFindById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Board with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void testDelete() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board testBoard = service.create(member,workspace.getId(), "testBoard", "testDescription");
        assertNotNull(testBoard);
        UUID id = testBoard.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void testUpdate() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = service.create(member, workspace.getId(), "name", "description");
        assertNotNull(board);
        board.setName("newBoard");
        board.setDescription("newDescription");
        board.setVisibility(BoardVisibility.PUBLIC);
        board.setFavourite(true);
        board.setArchived(true);
        Board testBoard = service.update(member, board);
        assertAll(
                () -> assertEquals("test@mail", testBoard.getCreatedBy()),
                () -> assertEquals("test@mail", testBoard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getUpdatedDate()),
                () -> assertEquals("newBoard", testBoard.getName()),
                () -> assertEquals("newDescription", testBoard.getDescription()),
                () -> assertTrue(testBoard.getMembers().contains(member)),
                () -> assertEquals(BoardVisibility.PUBLIC, testBoard.getVisibility()),
                () -> assertTrue(testBoard.getFavourite()),
                () -> assertTrue(testBoard.getArchived()),
                () -> assertEquals(workspace.getId(), testBoard.getWorkspaceId())
        );
        service.delete(testBoard.getId());
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Board testBoard = new Board();
        testBoard.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testBoard),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Board with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
