package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.BoardService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardTest {
    @Autowired
    private BoardService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        Workspace workspace = helper.getNewWorkspace("create@BT.com");

        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now());
        board.setName("testBoard");
        board.setDescription("testDescription");
        board.setWorkspaceId(workspace.getId());
        Set<UUID> membersId = workspace.getMembersId();
        board.setMembersId(membersId);
        Board testBoard = service.save(board);

        assertNotNull(testBoard);
        assertAll(
                () -> assertEquals(workspace.getCreatedBy(), testBoard.getCreatedBy()),
                () -> assertNull(testBoard.getUpdatedBy()),
                () -> assertEquals(board.getCreatedDate().toString(), testBoard.getCreatedDate().toString()),
                () -> assertNull(testBoard.getUpdatedDate()),
                () -> assertEquals("testBoard", testBoard.getName()),
                () -> assertEquals("testDescription", testBoard.getDescription()),
                () -> assertEquals(BoardVisibility.PRIVATE, testBoard.getVisibility()),
                () -> assertFalse(testBoard.getFavourite()),
                () -> assertFalse(testBoard.getArchived()),
                () -> assertEquals(workspace.getId(), testBoard.getWorkspaceId()),
                () -> assertEquals(workspace.getMembersId(), testBoard.getMembersId())
        );
    }

    @Test
    public void update() {
        Board board = helper.getNewBoard("update@BT.com");
        Member secondMember = helper.getNewMember("2update@BT.com");

        assertNotNull(board);
        board.setName("newBoard");
        board.setDescription("newDescription");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now().withNano(0));
        board.setVisibility(BoardVisibility.PUBLIC);
        board.setFavourite(true);
        board.setArchived(true);
        Set<UUID> membersId = board.getMembersId();
        membersId.add(secondMember.getId());
        board.setMembersId(membersId);
        Board testBoard = service.update(board);

        assertAll(
                () -> assertEquals(board.getCreatedBy(), testBoard.getCreatedBy()),
                () -> assertEquals(board.getUpdatedBy(), testBoard.getUpdatedBy()),
                () -> assertEquals(board.getCreatedDate().toString(), testBoard.getCreatedDate().toString()),
                () -> assertEquals(board.getUpdatedDate().toString(), testBoard.getUpdatedDate().toString()),
                () -> assertEquals("newBoard", testBoard.getName()),
                () -> assertEquals("newDescription", testBoard.getDescription()),
                () -> assertEquals(BoardVisibility.PUBLIC, testBoard.getVisibility()),
                () -> assertTrue(testBoard.getFavourite()),
                () -> assertTrue(testBoard.getArchived()),
                () -> assertEquals(board.getWorkspaceId(), testBoard.getWorkspaceId()),
                () -> assertEquals(2, testBoard.getMembersId().size()),
                () -> assertTrue(testBoard.getMembersId().contains(secondMember.getId()))
        );
    }

    @Test
    public void findAll() {
        Board firstBoard = helper.getNewBoard("1findAll@BT.com");
        Board secondBoard = helper.getNewBoard("2findAll@BT.com");

        assertNotNull(firstBoard);
        assertNotNull(secondBoard);
        List<Board> testBoard = service.getAll();
        assertAll(
                () -> assertTrue(testBoard.contains(firstBoard)),
                () -> assertTrue(testBoard.contains(secondBoard))
        );
    }

    @Test
    public void findById() {
        Board board = helper.getNewBoard("findById@BT.com");

        Board testBoard = service.getById(board.getId());
        assertEquals(board, testBoard);
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
    public void delete() {
        Board board = helper.getNewBoard("delete@BT.com");

        assertNotNull(board);
        service.delete(board.getId());
        assertFalse(service.getAll().contains(board));
    }

    @Test
    public void deleteFailure() {
        UUID id = UUID.randomUUID();
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.delete(id),
                "no exception"
        );
        assertEquals("No class spd.trello.domain.Board entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void badFieldsCreate() {
        Workspace workspace = helper.getNewWorkspace("badFieldsCreate@BT.com");
        Board board = new Board();
        board.setCreatedBy("c");
        board.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        board.setName("n");
        board.setDescription("d");
        board.setWorkspaceId(workspace.getId());
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(UUID.randomUUID());
        board.setMembersId(membersId);

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String mameException = "The name field must be between 2 and 20 characters long. \n";
        String descriptionException = "The description field must be between 2 and 255 characters long. \n";
        String memberIdException = "- memberId must belong to the member.";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(mameException)),
                () -> assertTrue(ex.getMessage().contains(descriptionException)),
                () -> assertTrue(ex.getMessage().contains(memberIdException))
        );
    }

    @Test
    public void archivedBoardCreate() {
        Workspace workspace = helper.getNewWorkspace("archivedBoardC@BT.com");
        Board board = new Board();
        board.setArchived(true);
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertEquals("You cannot create an archived board.", ex.getMessage());
    }

    @Test
    public void badWorkspaceIdCreate() {
        Workspace workspace = helper.getNewWorkspace("badWorkspaceIdCreate@BT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(UUID.randomUUID());
        board.setMembersId(workspace.getMembersId());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(board), "no exception"
        );
        assertEquals("WorkspaceId must be owned by Workspace.", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldCreate() {
        Workspace workspace = helper.getNewWorkspace("archivedBoardCreate@BT.com");
        Board board = new Board();
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldCreate() {
        Workspace workspace = helper.getNewWorkspace("nullCreatedDateFieldCreate@BT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullNameFieldCreate() {
        Workspace workspace = helper.getNewWorkspace("nullNameFieldCreate@BT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void nullMembersCreate() {
        Workspace workspace = helper.getNewWorkspace("nullMembersCreate@BT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(new HashSet<>());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The resource must belong to at least one member!", ex.getMessage());
    }

    @Test
    public void badFieldsUpdate() {
        Board board = helper.getNewBoard("badFieldsUpdate@BT.com");
        board.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        board.setCreatedBy("newCreateBy");
        board.setCreatedDate(LocalDateTime.now());
        board.setUpdatedBy("u");
        board.setName("n");
        board.setDescription("d");
        board.setWorkspaceId(UUID.randomUUID());
        Set<UUID> memberId = board.getMembersId();
        memberId.add(UUID.randomUUID());

        String updatedDateException = "The updatedDate should not be past or future. \n";
        String createdByException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String descriptionException = "The description field must be between 2 and 255 characters long. \n";
        String transferException = "Board cannot be transferred to another workspace. \n";
        String memberIdException = " - memberId must belong to the member. \n";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(board), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(nameException)),
                () -> assertTrue(ex.getMessage().contains(descriptionException)),
                () -> assertTrue(ex.getMessage().contains(memberIdException)),
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(updatedByException)),
                () -> assertTrue(ex.getMessage().contains(transferException))
        );
    }

    @Test
    public void nonExistentWorkspaceUpdate() {
        Board board = helper.getNewBoard("nonExistentWU@BT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(board), "no exception"
        );
        assertEquals("Cannot update non-existent board!", ex.getMessage());
    }

    @Test
    public void archivedBoardUpdate() {
        Board board = helper.getNewBoard("nonExistentWorkspaceU@BT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setArchived(true);
        service.update(board);
        board.setName("newName");

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(board), "no exception"
        );
        assertEquals("Archived board cannot be updated.", ex.getMessage());
    }

    @Test
    public void nullCreatedByFieldUpdate() {
        Board board = helper.getNewBoard("nullCreatedByFieldUpdate@BT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setCreatedBy(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullCreatedDateFieldUpdate() {
        Board board = helper.getNewBoard("nullCreatedDateFieldUpdate@BT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setCreatedDate(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The createdBy, createdDate fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedByFieldUpdate() {
        Board board = helper.getNewBoard("nullUpdatedByFieldU@BT.com");
        board.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(board), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldUpdate() {
        Board board = helper.getNewBoard("nullUpdatedDateFieldU@BT.com");
        board.setUpdatedBy(board.getCreatedBy());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(board), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }

    @Test
    public void nullNameFieldUpdate() {
        Board board = helper.getNewBoard("nullNameFieldUpdate@BT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setName(null);

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The name field must be filled.", ex.getMessage());
    }

    @Test
    public void nullMembersUpdate() {
        Board board = helper.getNewBoard("nullMembersUpdate@BT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setMembersId(new HashSet<>());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(board), "no exception"
        );
        assertEquals("The resource must belong to at least one member!", ex.getMessage());
    }
}
