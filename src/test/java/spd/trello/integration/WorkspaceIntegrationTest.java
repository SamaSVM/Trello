package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.WorkspaceVisibility;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkspaceIntegrationTest extends AbstractIntegrationTest<Workspace> {
    private final String URL_TEMPLATE = "/workspaces";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void createWithoutArg() throws Exception {
        Member member = helper.getNewMember("createWithoutArg@WIT");
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        workspace.setName("name");
        MvcResult mvcResult = super.create(URL_TEMPLATE, workspace);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(workspace.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertTrue(getValue(mvcResult, "$.createdDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(workspace.getName(), getValue(mvcResult, "$.name")),
                () -> assertNull(getValue(mvcResult, "$.description")),
                () -> assertEquals(WorkspaceVisibility.PRIVATE.toString(),
                        getValue(mvcResult, "$.visibility")),
                () -> assertTrue(testMembersId.contains(member.getId())),
                () -> assertEquals(1, testMembersId.size())
        );
    }

    @Test
    public void createFromArg() throws Exception {
        Member firstMember = helper.getNewMember("1create@WIT");
        Member secondMember = helper.getNewMember("2create@WIT");
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(firstMember.getCreatedBy());
        Set<UUID> membersId = new HashSet<>();
        membersId.add(firstMember.getId());
        membersId.add(secondMember.getId());
        workspace.setMembersId(membersId);
        workspace.setName("name");
        workspace.setDescription("description");
        workspace.setVisibility(WorkspaceVisibility.PUBLIC);
        MvcResult mvcResult = super.create(URL_TEMPLATE, workspace);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(workspace.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertTrue(getValue(mvcResult, "$.createdDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(workspace.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(workspace.getDescription(), getValue(mvcResult, "$.description")),
                () -> assertEquals(workspace.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue(testMembersId.contains(firstMember.getId())),
                () -> assertTrue(testMembersId.contains(secondMember.getId())),
                () -> assertEquals(2, testMembersId.size())
        );
    }

    @Test
    public void createFailure() throws Exception {
        Workspace entity = new Workspace();
        MvcResult mvcResult = super.create(URL_TEMPLATE, entity);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        Workspace firstWorkspace = helper.getNewWorkspace("1findAll@WIT");
        Workspace secondWorkspace = helper.getNewWorkspace("2findAll@WIT");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Workspace> testWorkspaces = helper.getWorkspacesArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testWorkspaces.contains(firstWorkspace)),
                () -> assertTrue(testWorkspaces.contains(secondWorkspace))
        );
    }

    @Test
    public void findById() throws Exception {
        Workspace workspace = helper.getNewWorkspace("findById@WIT");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, workspace.getId());
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(workspace.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(workspace.getName(), getValue(mvcResult, "$.name")),
                () -> assertNull(getValue(mvcResult, "$.description")),
                () -> assertEquals(WorkspaceVisibility.PRIVATE.toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertEquals(1, testMembersId.size())
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        Workspace workspace = helper.getNewWorkspace("deleteById@WIT");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, workspace.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Workspace> testWorkspaces = helper.getWorkspacesArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testWorkspaces.contains(workspace))
        );
    }

    @Test
    public void deleteByIdFailure() throws Exception {
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, UUID.randomUUID());

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus())
        );
    }

    @Test
    public void update() throws Exception {
        Workspace workspace = helper.getNewWorkspace("1update@WorkspaceIntegrationTest");
        Member secondMember = helper.getNewMember("2update@WorkspaceIntegrationTest");
        workspace.setUpdatedBy(workspace.getCreatedBy());
        workspace.setName("new name");
        workspace.setDescription("new description");
        workspace.setVisibility(WorkspaceVisibility.PUBLIC);
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(secondMember.getId());
        workspace.setMembersId(membersId);
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());
        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(workspace.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(workspace.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertTrue(getValue(mvcResult, "$.updatedDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals(workspace.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(workspace.getDescription(), getValue(mvcResult, "$.description")),
                () -> assertEquals(WorkspaceVisibility.PUBLIC.toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertEquals(2, membersId.size())
        );
    }

    @Test
    public void updateFailure() throws Exception {
        Workspace firstWorkspace = helper.getNewWorkspace("1updateFailure@WorkspaceIntegrationTest");
        firstWorkspace.setName(null);
        firstWorkspace.setUpdatedBy(firstWorkspace.getCreatedBy());

        Workspace secondWorkspace = new Workspace();
        secondWorkspace.setId(firstWorkspace.getId());

        MvcResult firstMvcResult = super.update(URL_TEMPLATE, firstWorkspace.getId(), firstWorkspace);
        MvcResult secondMvcResult = super.update(URL_TEMPLATE, secondWorkspace.getId(), secondWorkspace);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), secondMvcResult.getResponse().getStatus())
        );
    }
}
