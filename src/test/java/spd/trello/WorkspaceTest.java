package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
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
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("test4@mail");
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(workspace);
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
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("test5@mail");
        workspace.setName("1Name");
        workspace.setDescription("1Des");
        Workspace testFirstWorkspace = service.create(workspace);
        workspace.setName("2Name");
        workspace.setDescription("2Des");
        Workspace testSecondWorkspace = service.create(workspace);
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
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("test6@mail");
        workspace.setDescription("Description");
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(workspace),
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
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("test7@mail");
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(workspace);
        assertNotNull(testWorkspace);
        UUID id = testWorkspace.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        Workspace firstWorkspace = new Workspace();
        firstWorkspace.setCreatedBy("test8@mail");
        firstWorkspace.setName("testWorkspace");
        firstWorkspace.setDescription("testDescription");
        Workspace workspace = service.create(firstWorkspace);
        assertNotNull(workspace);
        workspace.setName("newWorkspace");
        workspace.setUpdatedBy("test8@mail");
        workspace.setDescription("newDescription");
        workspace.setVisibility(WorkspaceVisibility.PUBLIC);
        Workspace testWorkspace = service.update(workspace);
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
        Workspace testWorkspace = new Workspace();
        testWorkspace.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testWorkspace),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Workspace with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }

    @Test
    public void addAndDeleteSecondMember() {
        User secondUser = helper.getNewUser("addAndDeleteSecondMember2@WT");
        Member secondMember = helper.getNewMember(secondUser);
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("addAndDeleteSecondMember1@WT");
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Workspace testWorkspace = service.create(workspace);
        assertNotNull(testWorkspace);
        assertAll(
                () -> assertTrue(service.addMember(secondMember.getId(), testWorkspace.getId())),
                () -> assertTrue(service.deleteMember(secondMember.getId(), testWorkspace.getId()))
        );
    }
}
