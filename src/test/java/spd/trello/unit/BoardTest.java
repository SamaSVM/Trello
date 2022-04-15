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

import java.sql.Date;
import java.time.LocalDate;
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
        Workspace workspace = helper.getNewWorkspace("create@BT");

        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
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
                () -> assertTrue(testBoard.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
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
    public void findAll() {
        Board firstBoard = helper.getNewBoard("1findAll@BT");
        Board secondBoard = helper.getNewBoard("2findAll@BT");

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
        Board board = helper.getNewBoard("findById@BT");

        Board testBoard = service.getById(board.getId());
        assertEquals(board, testBoard);
    }

    @Test
    public void delete() {
        Board board = helper.getNewBoard("delete@BT");

        assertNotNull(board);
        service.delete(board.getId());
        assertFalse(service.getAll().contains(board));
    }

    @Test
    public void update() {
        Board board = helper.getNewBoard("update@BT");
        Member secondMember = helper.getNewMember("2update@BT");

        assertNotNull(board);
        board.setName("newBoard");
        board.setDescription("newDescription");
        board.setUpdatedBy(board.getCreatedBy());
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
                () -> assertTrue(testBoard.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertTrue(testBoard.getUpdatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
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
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Board()),
                "no exception"
        );
        assertTrue(ex.getMessage().contains("could not execute statement;"));

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
        assertEquals("No class spd.trello.domain.Board entity with id " + id + " exists!", ex.getMessage());
    }
}
