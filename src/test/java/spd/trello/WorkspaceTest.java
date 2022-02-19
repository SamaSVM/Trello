package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.WorkspaceVisibility;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.WorkspaceService;

import java.sql.Date;
import java.time.LocalDate;
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
    private Helper helper;

    @Test
    public void create() {
        User user = helper.getNewUser("create@WT");
        Member member = helper.getNewMember(user);

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(user.getEmail());
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Set<UUID> membersIds = workspace.getMembersIds();
        membersIds.add(member.getId());
        workspace.setMembersIds(membersIds);
        Workspace testWorkspace = service.save(workspace);

        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals(user.getEmail(), testWorkspace.getCreatedBy()),
                () -> assertNull(testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertNull(testWorkspace.getUpdatedDate()),
                () -> assertEquals("testWorkspace", testWorkspace.getName()),
                () -> assertEquals("testDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PRIVATE, testWorkspace.getVisibility()),
                () -> assertTrue(testWorkspace.getMembersIds().contains(member.getId()))
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("findAll@WT");
        Member member = helper.getNewMember(user);

        Workspace firstWorkspace = new Workspace();
        firstWorkspace.setCreatedBy(user.getEmail());
        firstWorkspace.setName("1Name");
        firstWorkspace.setDescription("1Des");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        firstWorkspace.setMembersIds(membersIds);
        Workspace testFirstWorkspace = service.save(firstWorkspace);

        Workspace secondWorkspace = new Workspace();
        secondWorkspace.setCreatedBy(user.getEmail());
        secondWorkspace.setName("2Name");
        secondWorkspace.setDescription("2Des");
        secondWorkspace.setMembersIds(membersIds);
        Workspace testSecondWorkspace = service.save(secondWorkspace);

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
        User user = helper.getNewUser("findById@WT");
        Member member = helper.getNewMember(user);

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(user.getEmail());
        workspace.setName("Name");
        workspace.setDescription("Description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        workspace.setMembersIds(membersIds);
        service.save(workspace);

        Workspace testMember = service.getById(workspace.getId());
        assertEquals(workspace, testMember);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@WT");
        Member member = helper.getNewMember(user);

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(user.getEmail());
        workspace.setName("Name");
        workspace.setDescription("Description");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(member.getId());
        workspace.setMembersIds(membersIds);
        Workspace testWorkspace = service.save(workspace);

        assertNotNull(testWorkspace);
        service.delete(testWorkspace.getId());
        assertFalse(service.getAll().contains(testWorkspace));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@WT");
        Member firstMember = helper.getNewMember(user);
        Member secondMember = helper.getNewMember(user);

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(user.getEmail());
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(firstMember.getId());
        workspace.setMembersIds(membersIds);
        Workspace updateWorkspace = service.save(workspace);

        assertNotNull(updateWorkspace);
        updateWorkspace.setUpdatedBy(user.getEmail());
        updateWorkspace.setName("newWorkspace");
        updateWorkspace.setDescription("newDescription");
        updateWorkspace.setVisibility(WorkspaceVisibility.PUBLIC);
        membersIds.add(secondMember.getId());
        updateWorkspace.setMembersIds(membersIds);
        Workspace testWorkspace = service.update(updateWorkspace);

        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals(user.getEmail(), testWorkspace.getCreatedBy()),
                () -> assertEquals(user.getEmail(), testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getUpdatedDate()),
                () -> assertEquals("newWorkspace", testWorkspace.getName()),
                () -> assertEquals("newDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PUBLIC, testWorkspace.getVisibility()),
                () -> assertTrue(testWorkspace.getMembersIds().contains(firstMember.getId())),
                () -> assertTrue(testWorkspace.getMembersIds().contains(secondMember.getId()))
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Workspace()),
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
        assertEquals("No class spd.trello.domain.Workspace entity with id " + id + " exists!", ex.getMessage());
    }
}
