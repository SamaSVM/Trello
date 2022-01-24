package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.MemberRole;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.services.WorkspaceService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static spd.trello.Helper.*;

public class WorkspaceTest extends BaseTest {
    private final WorkspaceService service = context.getBean(WorkspaceService.class);

    @Test
    public void successCreate() {
        User user = getNewUser("test4@mail");
        Member member = getNewMember(user);
        Workspace testWorkspace = service.create(member, "testWorkspace", "testDescription");
        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals("test4@mail", testWorkspace.getCreatedBy()),
                () -> assertNull(testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertNull(testWorkspace.getUpdatedDate()),
                () -> assertEquals("testWorkspace", testWorkspace.getName()),
                () -> assertEquals("testDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PRIVATE, testWorkspace.getVisibility())
        );
    }

    @Test
    public void findAll() {
        User user = getNewUser("test5@mail");
        Member member = getNewMember(user);
        Workspace testFirstWorkspace = service.create(member, "1Name", "1Des");
        Workspace testSecondWorkspace = service.create(member, "2Name", "2Des");
        assertNotNull(testFirstWorkspace);
        assertNotNull(testSecondWorkspace);
        List<Workspace> testWorkspace = service.findAll();
        assertAll(
                () -> assertTrue(testWorkspace.contains(testFirstWorkspace)),
                () -> assertTrue(testWorkspace.contains(testSecondWorkspace))
        );
    }

    @Test
    public void createFailure() {
        User user = getNewUser("test6@mail");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Description"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Workspace doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Workspace with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = getNewUser("test7@mail");
        Member member = getNewMember(user);
        Workspace testWorkspace = service.create(member, "testWorkspace", "testDescription");
        assertNotNull(testWorkspace);
        UUID id = testWorkspace.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = getNewUser("test8@mail");
        Member member = getNewMember(user);
        Workspace workspace = service.create(member, "testWorkspace", "testDescription");
        assertNotNull(workspace);
        workspace.setName("newWorkspace");
        workspace.setDescription("newDescription");
        workspace.setVisibility(WorkspaceVisibility.PUBLIC);
        Workspace testWorkspace = service.update(member, workspace);
        assertAll(
                () -> assertEquals("test8@mail", testWorkspace.getCreatedBy()),
                () -> assertEquals("test8@mail", testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getUpdatedDate()),
                () -> assertEquals("newWorkspace", testWorkspace.getName()),
                () -> assertEquals("newDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PUBLIC, testWorkspace.getVisibility())
        );
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Workspace testWorkspace = new Workspace();
        testWorkspace.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testWorkspace),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("This member cannot update workspace!", ex.getMessage());
    }

    @Test
    public void addAndDeleteSecondMember() {
        User firstUser = getNewUser("addAndDeleteSecondMember1@WT");
        User secondUser = getNewUser("addAndDeleteSecondMember2@WT");
        Member firstMember = getNewMember(firstUser);
        Member secondMember = getNewMember(secondUser);
        Workspace testWorkspace = service.create(firstMember, "testWorkspace", "testDescription");
        assertNotNull(testWorkspace);
        assertAll(
                () -> assertTrue(service.addMember(firstMember, secondMember.getId(), testWorkspace.getId())),
                () -> assertTrue(service.deleteMember(firstMember, secondMember.getId(), testWorkspace.getId()))
        );
    }

    @Test
    public void getAllMembersForWorkspace() {
        User firstUser = getNewUser("getAllMembersForWorkspace1@WT");
        User secondUser = getNewUser("getAllMembersForWorkspace2@WT");
        Member firstMember = getNewMember(firstUser);
        Member secondMember = getNewMember(secondUser);
        Workspace testWorkspace = service.create(firstMember, "testWorkspace", "testDescription");
        service.addMember(firstMember, secondMember.getId(), testWorkspace.getId());
        assertNotNull(testWorkspace);
        List<Member> members = service.getAllMembers(firstMember, testWorkspace.getId());
        assertAll(
                () -> assertTrue(members.contains(firstMember)),
                () -> assertTrue(members.contains(secondMember)),
                () -> assertEquals(2, members.size())
        );
    }

    @Test
    public void getAllBoardsForWorkspace() {
        User user = getNewUser("getAllBoardsForWorkspace@WT");
        Member member = getNewMember(user);
        Workspace testWorkspace = service.create(member, "testWorkspace", "testDescription");
        Board firstBoard = getNewBoard(member, testWorkspace.getId());
        Board secondBoard = getNewBoard(member, testWorkspace.getId());
        assertNotNull(testWorkspace);
        List<Board> boards = service.getAllBoards(member, testWorkspace.getId());
        assertAll(
                () -> assertTrue(boards.contains(firstBoard)),
                () -> assertTrue(boards.contains(secondBoard)),
                () -> assertEquals(2, boards.size())
        );
    }
}
