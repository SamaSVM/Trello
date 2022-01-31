package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.domain.enums.MemberRole;
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
        Board testBoard = service.create(member, workspace.getId(), "testBoard", "testDescription");
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
    public void testFindAll() {
        User user = helper.getNewUser("test10@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board testFirstBoard = service.create(member, workspace.getId(), "1Board", "1Description");
        Board testSecondBoard = service.create(member, workspace.getId(), "2Board", "2Description");
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
        User user = helper.getNewUser("test11@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Name", "Description"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Board doesn't creates", ex.getMessage());
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
        User user = helper.getNewUser("test12@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board testBoard = service.create(member, workspace.getId(), "testBoard", "testDescription");
        assertNotNull(testBoard);
        UUID id = testBoard.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void testUpdate() {
        User user = helper.getNewUser("test13@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board board = service.create(member, workspace.getId(), "name", "description");
        assertNotNull(board);
        board.setName("newBoard");
        board.setDescription("newDescription");
        board.setVisibility(BoardVisibility.PUBLIC);
        board.setFavourite(true);
        board.setArchived(true);
        Board testBoard = service.update(member, board);
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
        assertEquals("This member cannot update board!", ex.getMessage());
    }

    @Test
    public void addAndDeleteSecondMember() {
        User firstUser = helper.getNewUser("addAndDeleteSecondMember1@BT");
        User secondUser = helper.getNewUser("addAndDeleteSecondMember2@BT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board testBoard = service.create(firstMember, workspace.getId(), "testBoard", "testDescription");
        assertNotNull(testBoard);
        assertAll(
                () -> assertTrue(service.addMember(firstMember, secondMember.getId(), testBoard.getId())),
                () -> assertTrue(service.deleteMember(firstMember, secondMember.getId(), testBoard.getId()))
        );
    }

    @Test
    public void getAllMembersForBoard() {
        User firstUser = helper.getNewUser("getAllMembersForBoard1@BT");
        User secondUser = helper.getNewUser("getAllMembersForBoard2@BT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = helper.getNewWorkspace(firstMember);
        Board testBoard = service.create(firstMember, workspace.getId(), "testBoard", "testDescription");
        service.addMember(firstMember, secondMember.getId(), testBoard.getId());
        assertNotNull(testBoard);
        List<Member> members = service.getAllMembers(firstMember, testBoard.getId());
        assertAll(
                () -> assertTrue(members.contains(firstMember)),
                () -> assertTrue(members.contains(secondMember)),
                () -> assertEquals(2, members.size())
        );
    }

    @Test
    public void getAllCardListsForBoard() {
        User user = helper.getNewUser("getAllCardListsForBoard@BT");
        Member member = helper.getNewMember(user);
        Workspace workspace = helper.getNewWorkspace(member);
        Board testBoard = service.create(member, workspace.getId(), "testBoard", "testDescription");
        CardList firstBoard = helper.getNewCardList(member, testBoard.getId());
        CardList secondBoard = helper.getNewCardList(member, testBoard.getId());
        assertNotNull(testBoard);
        List<CardList> boards = service.getAllCardLists(member, testBoard.getId());
        assertAll(
                () -> assertTrue(boards.contains(firstBoard)),
                () -> assertTrue(boards.contains(secondBoard)),
                () -> assertEquals(2, boards.size())
        );
    }
}
