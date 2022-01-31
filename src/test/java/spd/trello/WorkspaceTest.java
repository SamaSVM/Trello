package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
public class WorkspaceTest {
    @Autowired
    private WorkspaceService service;

    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test4@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = new Workspace();
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(member, workspace);
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
        User user = helper.getNewUser("test5@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = new Workspace();
        workspace.setName("1Name");
        workspace.setDescription("1Des");
        Workspace testFirstWorkspace = service.create(member, workspace);
        workspace.setName("2Name");
        workspace.setDescription("2Des");
        Workspace testSecondWorkspace = service.create(member, workspace);
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
        User user = helper.getNewUser("test6@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = new Workspace();
        workspace.setDescription("Description");
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, workspace),
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
        User user = helper.getNewUser("test7@mail");
        Member member = helper.getNewMember(user);
        Workspace workspace = new Workspace();
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(member, workspace);
        assertNotNull(testWorkspace);
        UUID id = testWorkspace.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test8@mail");
        Member member = helper.getNewMember(user);
        Workspace firstWorkspace = new Workspace();
        firstWorkspace.setName("testWorkspace");
        firstWorkspace.setDescription("testDescription");
        Workspace workspace = service.create(member, firstWorkspace);
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
        User firstUser = helper.getNewUser("addAndDeleteSecondMember1@WT");
        User secondUser = helper.getNewUser("addAndDeleteSecondMember2@WT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = new Workspace();
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(firstMember, workspace);
        assertNotNull(testWorkspace);
        assertAll(
                () -> assertTrue(service.addMember(firstMember, secondMember.getId(), testWorkspace.getId())),
                () -> assertTrue(service.deleteMember(firstMember, secondMember.getId(), testWorkspace.getId()))
        );
    }

    @Test
    public void getAllMembersForWorkspace() {
        User firstUser = helper.getNewUser("getAllMembersForWorkspace1@WT");
        User secondUser = helper.getNewUser("getAllMembersForWorkspace2@WT");
        Member firstMember = helper.getNewMember(firstUser);
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = new Workspace();
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(firstMember, workspace);
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
        User user = helper.getNewUser("getAllBoardsForWorkspace@WT");
        Member member = helper.getNewMember(user);
        Workspace workspace = new Workspace();
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(member, workspace);
        Board firstBoard = helper.getNewBoard(member, testWorkspace.getId());
        Board secondBoard = helper.getNewBoard(member, testWorkspace.getId());
        assertNotNull(testWorkspace);
        List<Board> boards = service.getAllBoards(member, testWorkspace.getId());
        assertAll(
                () -> assertTrue(boards.contains(firstBoard)),
                () -> assertTrue(boards.contains(secondBoard)),
                () -> assertEquals(2, boards.size())
        );
    }
}
