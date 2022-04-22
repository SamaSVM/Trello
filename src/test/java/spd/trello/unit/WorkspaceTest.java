package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.WorkspaceService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkspaceTest {
    @Autowired
    private WorkspaceService service;

    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        Member member = helper.getNewMember("create@WT.com");

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        workspace.setCreatedDate(LocalDateTime.now().withNano(0));
        Workspace testWorkspace = service.save(workspace);

        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals(member.getCreatedBy(), testWorkspace.getCreatedBy()),
                () -> assertNull(testWorkspace.getUpdatedBy()),
                () -> assertEquals(workspace.getCreatedDate(), testWorkspace.getCreatedDate()),
                () -> assertNull(testWorkspace.getUpdatedDate()),
                () -> assertEquals(workspace.getName(), testWorkspace.getName()),
                () -> assertEquals(workspace.getDescription(), testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PRIVATE, testWorkspace.getVisibility()),
                () -> assertTrue(testWorkspace.getMembersId().contains(member.getId())),
                () -> assertEquals(1, testWorkspace.getMembersId().size())
        );
    }

    @Test
    public void findAll() {
        Workspace testFirstWorkspace = helper.getNewWorkspace("findAll1@WT.com");
        Workspace testSecondWorkspace = helper.getNewWorkspace("findAll2@WT.com");

        assertNotNull(testFirstWorkspace);
        assertNotNull(testSecondWorkspace);
        List<Workspace> testWorkspace = service.getAll();
        assertAll(
                () -> assertTrue(testWorkspace.contains(testFirstWorkspace)),
                () -> assertTrue(testWorkspace.contains(testSecondWorkspace))
        );
    }

    @Test
    public void findById() {
        Workspace workspace = helper.getNewWorkspace("findById@WT.com");

        Workspace testMember = service.getById(workspace.getId());
        assertEquals(workspace, testMember);
    }

    @Test
    public void delete() {
        Workspace testWorkspace = helper.getNewWorkspace("delete@WT.com");

        assertNotNull(testWorkspace);
        service.delete(testWorkspace.getId());
        assertFalse(service.getAll().contains(testWorkspace));
    }

    @Test
    public void update() {
        Workspace workspace = helper.getNewWorkspace("1update@WT.com");
        Member member = helper.getNewMember("2update@WT.com");

        workspace.setUpdatedBy(member.getCreatedBy());
        workspace.setUpdatedDate(LocalDateTime.now().withNano(0));
        workspace.setName("newWorkspace");
        workspace.setDescription("newDescription");
        workspace.setVisibility(WorkspaceVisibility.PUBLIC);
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        Workspace testWorkspace = service.update(workspace);

        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals(workspace.getCreatedBy(), testWorkspace.getCreatedBy()),
                () -> assertEquals(workspace.getUpdatedBy(), testWorkspace.getUpdatedBy()),
                () -> assertEquals(workspace.getCreatedDate(), testWorkspace.getCreatedDate()),
                () -> assertEquals(workspace.getUpdatedDate(), testWorkspace.getUpdatedDate()),
                () -> assertEquals(workspace.getName(), testWorkspace.getName()),
                () -> assertEquals(workspace.getDescription(), testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PUBLIC, testWorkspace.getVisibility()),
                () -> assertTrue(testWorkspace.getMembersId().contains(member.getId())),
                () -> assertEquals(2, testWorkspace.getMembersId().size())
        );
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
        assertEquals("No class spd.trello.domain.Workspace entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void validationCreate() {
        Member member = helper.getNewMember("validationCreate@WT.com");
        Workspace workspace = new Workspace();
        workspace.setCreatedDate(LocalDateTime.now().minusMinutes(2L));
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("name");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(UUID.randomUUID());
        workspace.setMembersId(membersId);

        String createdDateException = "The createdDate should not be past or future. \n";
        String memberIdException = "- memberId must belong to the member.";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(workspace), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(memberIdException))
        );

    }

    @Test
    public void nonExistentMemberCreate() {
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("createBy");
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setName("name");
        workspace.setMembersId(new HashSet<>());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.save(workspace), "no exception"
        );
        assertEquals("The resource must belong to at least one member! \n", ex.getMessage());
    }

    @Test
    public void nonExistentWorkspaceUpdate() {
        Workspace workspace = helper.getNewWorkspace("nonExistentWorkspace@WT.com");
        workspace.setId(UUID.randomUUID());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.update(workspace), "no exception"
        );
        assertEquals("Cannot update non-existent workspace!", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
        Workspace workspace = helper.getNewWorkspace("validationUpdate@WT.com");
        workspace.setUpdatedBy(workspace.getCreatedBy());
        workspace.setUpdatedDate(LocalDateTime.now().minusMinutes(2L));
        workspace.setCreatedBy("newCreatedBy");
        workspace.setCreatedDate(LocalDateTime.now());
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(UUID.randomUUID());
        workspace.setMembersId(membersId);

        String updatedDateException = "The updatedDate should not be past or future. \n";
        String createdByException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String memberIdException = "- memberId must belong to the member.";

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(workspace), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains(updatedDateException)),
                () -> assertTrue(ex.getMessage().contains(createdByException)),
                () -> assertTrue(ex.getMessage().contains(createdDateException)),
                () -> assertTrue(ex.getMessage().contains(memberIdException))
        );
    }

    @Test
    public void nullUpdatedByFieldUpdate() {
        Workspace workspace = helper.getNewWorkspace("nullUpdatedByField@WT.com");
        workspace.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(workspace), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldUpdate() {
        Workspace workspace = helper.getNewWorkspace("nullUpdatedDateFieldUpdate@WT.com");
        workspace.setUpdatedBy(workspace.getCreatedBy());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(workspace), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }
}
