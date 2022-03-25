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
    private UnitHelper helper;

    @Test
    public void create() {
        Member member = helper.getNewMember("create@WT");

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        Workspace testWorkspace = service.save(workspace);

        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals(member.getCreatedBy(), testWorkspace.getCreatedBy()),
                () -> assertNull(testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertNull(testWorkspace.getUpdatedDate()),
                () -> assertEquals("testWorkspace", testWorkspace.getName()),
                () -> assertEquals("testDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PRIVATE, testWorkspace.getVisibility()),
                () -> assertTrue(testWorkspace.getMembersId().contains(member.getId()))
        );
    }

    @Test
    public void findAll() {
        Member member = helper.getNewMember("findAll@WT");

        Workspace firstWorkspace = new Workspace();
        firstWorkspace.setCreatedBy(member.getCreatedBy());
        firstWorkspace.setName("1Name");
        firstWorkspace.setDescription("1Des");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        firstWorkspace.setMembersId(membersId);
        Workspace testFirstWorkspace = service.save(firstWorkspace);

        Workspace secondWorkspace = new Workspace();
        secondWorkspace.setCreatedBy(member.getCreatedBy());
        secondWorkspace.setName("2Name");
        secondWorkspace.setDescription("2Des");
        secondWorkspace.setMembersId(membersId);
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
        Member member = helper.getNewMember("findById@WT");

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("Name");
        workspace.setDescription("Description");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        service.save(workspace);

        Workspace testMember = service.getById(workspace.getId());
        assertEquals(workspace, testMember);
    }

    @Test
    public void delete() {
        Member member = helper.getNewMember("delete@WT");

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("Name");
        workspace.setDescription("Description");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        Workspace testWorkspace = service.save(workspace);

        assertNotNull(testWorkspace);
        service.delete(testWorkspace.getId());
        assertFalse(service.getAll().contains(testWorkspace));
    }

    @Test
    public void update() {
        Member firstMember = helper.getNewMember("1update@WT");
        Member secondMember = helper.getNewMember("2update@WT");

        Workspace workspace = new Workspace();
        workspace.setCreatedBy(firstMember.getCreatedBy());
        workspace.setName("testWorkspace");
        workspace.setDescription("testDescription");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(firstMember.getId());
        workspace.setMembersId(membersId);
        Workspace updateWorkspace = service.save(workspace);

        assertNotNull(updateWorkspace);
        updateWorkspace.setUpdatedBy(secondMember.getCreatedBy());
        updateWorkspace.setName("newWorkspace");
        updateWorkspace.setDescription("newDescription");
        updateWorkspace.setVisibility(WorkspaceVisibility.PUBLIC);
        membersId.add(secondMember.getId());
        updateWorkspace.setMembersId(membersId);
        Workspace testWorkspace = service.update(updateWorkspace);

        assertNotNull(testWorkspace);
        assertAll(
                () -> assertEquals(firstMember.getCreatedBy(), testWorkspace.getCreatedBy()),
                () -> assertEquals(secondMember.getCreatedBy(), testWorkspace.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testWorkspace.getUpdatedDate()),
                () -> assertEquals("newWorkspace", testWorkspace.getName()),
                () -> assertEquals("newDescription", testWorkspace.getDescription()),
                () -> assertEquals(WorkspaceVisibility.PUBLIC, testWorkspace.getVisibility()),
                () -> assertTrue(testWorkspace.getMembersId().contains(firstMember.getId())),
                () -> assertTrue(testWorkspace.getMembersId().contains(secondMember.getId()))
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
