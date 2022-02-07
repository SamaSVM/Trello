package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.services.BoardService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BoardTest {
    @Autowired
    private BoardService service;

    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test9@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = new Board();
        board.setCreatedBy("test9@mail");
        board.setName("testBoard");
        board.setDescription("testDescription");
        board.setWorkspaceId(workspace.getId());
        Board testBoard = service.create(board);
        assertNotNull(testBoard);
        assertAll(
                () -> assertEquals("test9@mail", testBoard.getCreatedBy()),
                () -> assertNull(testBoard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getCreatedDate()),
                () -> assertNull(testBoard.getUpdatedDate()),
                () -> assertEquals("testBoard", testBoard.getName()),
                () -> assertEquals("testDescription", testBoard.getDescription()),
                () -> assertEquals(BoardVisibility.PRIVATE, testBoard.getVisibility()),
                () -> assertFalse(testBoard.getFavourite()),
                () -> assertFalse(testBoard.getArchived()),
                () -> assertEquals(workspace.getId(), testBoard.getWorkspaceId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("test10@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = new Board();
        board.setCreatedBy("test10@mail");
        board.setWorkspaceId(workspace.getId());
        board.setName("1Board");
        board.setDescription("1Description");
        Board testFirstBoard = service.create(board);
        board.setName("2Board");
        board.setDescription("2Description");
        Board testSecondBoard = service.create(board);
        assertNotNull(testFirstBoard);
        assertNotNull(testSecondBoard);
        List<Board> testBoard = service.findAll();
        assertAll(
                () -> assertTrue(testBoard.contains(testFirstBoard)),
                () -> assertTrue(testBoard.contains(testSecondBoard))
        );
    }

    @Test
    public void createFailure() {
        Board board = new Board();
        board.setName("Name");
        board.setDescription("Description");
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(board),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Board doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Board with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test12@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = new Board();
        board.setCreatedBy("test12@mail");
        board.setWorkspaceId(workspace.getId());
        board.setName("testBoard");
        board.setDescription("testDescription");
        Board testBoard = service.create(board);
        assertNotNull(testBoard);
        UUID id = testBoard.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test13@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = new Board();
        board.setCreatedBy("test13@mail");
        board.setWorkspaceId(workspace.getId());
        board.setName("name");
        board.setDescription("description");
        Board updateBoard = service.create(board);
        assertNotNull(updateBoard);
        updateBoard.setName("newBoard");
        updateBoard.setDescription("newDescription");
        updateBoard.setUpdatedBy("test13@mail");
        updateBoard.setVisibility(BoardVisibility.PUBLIC);
        updateBoard.setFavourite(true);
        updateBoard.setArchived(true);
        Board testBoard = service.update(updateBoard);
        assertAll(
                () -> assertEquals("test13@mail", testBoard.getCreatedBy()),
                () -> assertEquals("test13@mail", testBoard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getUpdatedDate()),
                () -> assertEquals("newBoard", testBoard.getName()),
                () -> assertEquals("newDescription", testBoard.getDescription()),
                () -> assertEquals(BoardVisibility.PUBLIC, testBoard.getVisibility()),
                () -> assertTrue(testBoard.getFavourite()),
                () -> assertTrue(testBoard.getArchived()),
                () -> assertEquals(workspace.getId(), testBoard.getWorkspaceId())
        );
    }

    @Test
    public void updateFailure() {
        Board testBoard = new Board();
        testBoard.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testBoard),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Board with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void addAndDeleteSecondMember() {
        User firstUser = helper.getNewUser("addAndDeleteSecondMember1@BT");
        User secondUser = helper.getNewUser("addAndDeleteSecondMember2@BT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board board = new Board();
        board.setCreatedBy("addAndDeleteSecondMember1@BT");
        board.setName("testBoard");
        board.setDescription("testDescription");
        board.setWorkspaceId(workspace.getId());
        Board testBoard = service.create(board);
        assertNotNull(testBoard);
        assertAll(
                () -> assertTrue(service.addMember(secondMember.getId(), testBoard.getId())),
                () -> assertTrue(service.deleteMember(secondMember.getId(), testBoard.getId()))
        );
    }

    @Test
    public void getAllBoardsForWorkspace() {
        User user = helper.getNewUser("getAllBoardsForWorkspace@BT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = new Board();
        board.setWorkspaceId(workspace.getId());
        board.setCreatedBy("getAllBoardsForWorkspace@BT");
        board.setName("Name");
        board.setDescription("Description");
        Board firstBoard = service.create(board);
        Board secondBoard = service.create(board);
        assertNotNull(firstBoard);
        assertNotNull(secondBoard);
        List<Board> boards = service.getAllBoardsForWorkspace(workspace.getId());
        assertAll(
                () -> assertTrue(boards.contains(firstBoard)),
                () -> assertTrue(boards.contains(secondBoard)),
                () -> assertEquals(2, boards.size())
        );
    }
}
