package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.MemberRole;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.repository.WorkspaceRepository;
import spd.trello.services.WorkspaceService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static spd.trello.Helper.*;

public class WorkspaceTest extends BaseTest {
    private final WorkspaceService service;

    public WorkspaceTest() {
        service = new WorkspaceService(new WorkspaceRepository(dataSource));
    }

    @Test
    public void successCreate() {
        User user = getNewUser();
        Member member = getNewMember(user);
        Workspace testWorkspace = service.create(member, "testWorkspace", "testDescription");
        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals("test@mail", testWorkspace.getCreatedBy()),
                () -> assertNull(testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertNull(testWorkspace.getUpdatedDate()),
                () -> assertEquals("testWorkspace", testWorkspace.getName()),
                () -> assertEquals("testDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PRIVATE, testWorkspace.getVisibility())
        );
        service.delete(testWorkspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void createFailure() {
        User user = getNewUser();
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Description"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Workspace doesn't creates", ex.getMessage());
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
        assertEquals("Workspace with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void testDelete() {
        User user = getNewUser();
        Member member = getNewMember(user);
        Workspace testWorkspace = service.create(member, "testWorkspace", "testDescription");
        assertNotNull(testWorkspace);
        UUID id = testWorkspace.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void testUpdate() {
        User user = getNewUser();
        Member member = getNewMember(user);
        Workspace workspace = service.create(member, "testWorkspace", "testDescription");
        assertNotNull(workspace);
        workspace.setName("newWorkspace");
        workspace.setDescription("newDescription");
        workspace.setVisibility(WorkspaceVisibility.PUBLIC);
        Workspace testWorkspace = service.update(member, workspace);
        assertAll(
                () -> assertEquals("test@mail", testWorkspace.getCreatedBy()),
                () -> assertEquals("test@mail", testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getUpdatedDate()),
                () -> assertEquals("newWorkspace", testWorkspace.getName()),
                () -> assertEquals("newDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PUBLIC, testWorkspace.getVisibility())
        );
        service.delete(testWorkspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
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
        assertEquals("Workspace with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
