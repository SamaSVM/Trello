package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.BoardService;

import java.sql.Date;
import java.time.LocalDate;
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
    private Helper helper;

    @Test
    public void create() {
        User user = helper.getNewUser("test9@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);

        Board board = new Board();
        board.setCreatedBy(user.getEmail());
        board.setName("testBoard");
        board.setDescription("testDescription");
        board.setWorkspaceId(workspace.getId());
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        board.setMembersIds(membersIds);
        Board testBoard = service.save(board);

        assertNotNull(testBoard);
        assertAll(
                () -> assertEquals(user.getEmail(), testBoard.getCreatedBy()),
                () -> assertNull(testBoard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getCreatedDate()),
                () -> assertNull(testBoard.getUpdatedDate()),
                () -> assertEquals("testBoard", testBoard.getName()),
                () -> assertEquals("testDescription", testBoard.getDescription()),
                () -> assertEquals(BoardVisibility.PRIVATE, testBoard.getVisibility()),
                () -> assertFalse(testBoard.getFavourite()),
                () -> assertFalse(testBoard.getArchived()),
                () -> assertEquals(workspace.getId(), testBoard.getWorkspaceId()),
                () -> assertTrue(testBoard.getMembersIds().contains(member.getId()))
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("test10@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);

        Board board = new Board();
        board.setCreatedBy(user.getEmail());
        board.setWorkspaceId(workspace.getId());
        board.setName("1Board");
        board.setDescription("1Description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        board.setMembersIds(membersIds);
        Board testFirstBoard = service.save(board);

        Board secondBoard = new Board();
        secondBoard.setCreatedBy(user.getEmail());
        secondBoard.setWorkspaceId(workspace.getId());
        secondBoard.setName("2Board");
        board.setCreatedBy(user.getEmail());
        Board testSecondBoard = service.save(secondBoard);

        assertNotNull(testFirstBoard);
        assertNotNull(testSecondBoard);
        List<Board> testBoard = service.getAll();
        assertAll(
                () -> assertTrue(testBoard.contains(testFirstBoard)),
                () -> assertTrue(testBoard.contains(testSecondBoard))
        );
    }

    @Test
    public void findById() {
        User user = helper.getNewUser("findById@BT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);

        Board board = new Board();
        board.setCreatedBy(user.getEmail());
        board.setWorkspaceId(workspace.getId());
        board.setName("Board");
        board.setDescription("Description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        board.setMembersIds(membersIds);
        service.save(board);

        Board testBoard = service.getById(board.getId());
        assertEquals(board, testBoard);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@BT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);

        Board board = new Board();
        board.setCreatedBy(user.getEmail());
        board.setWorkspaceId(workspace.getId());
        board.setName("Board");
        board.setDescription("Description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        board.setMembersIds(membersIds);
        Board testBoard = service.save(board);

        assertNotNull(testBoard);
        service.delete(testBoard.getId());
        assertFalse(service.getAll().contains(testBoard));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@BT");
        Member firstMember = helper.getNewMember(user);
        Member secondMember = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(firstMember);

        Board board = new Board();
        board.setCreatedBy(user.getEmail());
        board.setWorkspaceId(workspace.getId());
        board.setName("Board");
        board.setDescription("Description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(firstMember.getId());
        board.setMembersIds(membersIds);
        Board updateBoard = service.save(board);

        assertNotNull(updateBoard);
        updateBoard.setName("newBoard");
        updateBoard.setDescription("newDescription");
        updateBoard.setUpdatedBy(user.getEmail());
        updateBoard.setVisibility(BoardVisibility.PUBLIC);
        updateBoard.setFavourite(true);
        updateBoard.setArchived(true);
        membersIds.add(secondMember.getId());
        updateBoard.setMembersIds(membersIds);
        Board testBoard = service.update(updateBoard);

        assertAll(
                () -> assertEquals(user.getEmail(), testBoard.getCreatedBy()),
                () -> assertEquals(user.getEmail(), testBoard.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testBoard.getUpdatedDate()),
                () -> assertEquals("newBoard", testBoard.getName()),
                () -> assertEquals("newDescription", testBoard.getDescription()),
                () -> assertEquals(BoardVisibility.PUBLIC, testBoard.getVisibility()),
                () -> assertTrue(testBoard.getFavourite()),
                () -> assertTrue(testBoard.getArchived()),
                () -> assertEquals(workspace.getId(), testBoard.getWorkspaceId()),
                () -> assertTrue(testBoard.getMembersIds().contains(firstMember.getId())),
                () -> assertTrue(testBoard.getMembersIds().contains(secondMember.getId()))
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
