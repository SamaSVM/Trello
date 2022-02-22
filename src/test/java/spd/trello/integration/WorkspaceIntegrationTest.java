package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.WorkspaceVisibility;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkspaceIntegrationTest extends AbstractIntegrationTest<User>{
    private final String URL_TEMPLATE = "/workspaces";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Member firstMember = helper.getNewMember("1create@WIT");
        Workspace firstWorkspace = new Workspace();
        firstWorkspace.setCreatedBy(firstMember.getCreatedBy());
        Set<UUID> membersId = new HashSet<>();
        membersId.add(firstMember.getId());
        firstWorkspace.setMembersId(membersId);
        firstWorkspace.setName("name");
        MvcResult firstMvcResult = super.create(URL_TEMPLATE, firstWorkspace);
        Set<UUID> testFirstMembersId = helper.getIdsFromJson(getValue(firstMvcResult, "$.membersId").toString());

        Member secondMember = helper.getNewMember("2create@WIT");
        Workspace secondWorkspace = new Workspace();
        secondWorkspace.setCreatedBy(firstMember.getCreatedBy());
        membersId.add(secondMember.getId());
        secondWorkspace.setMembersId(membersId);
        secondWorkspace.setName("name");
        secondWorkspace.setDescription("description");
        secondWorkspace.setVisibility(WorkspaceVisibility.PUBLIC);
        MvcResult secondMvcResult = super.create(URL_TEMPLATE, secondWorkspace);
        Set<UUID> testSecondMembersId = helper.getIdsFromJson(getValue(secondMvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(firstMvcResult, "$.id")),
                () -> assertEquals(firstWorkspace.getCreatedBy(), getValue(firstMvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(firstMvcResult, "$.createdDate")),
                () -> assertNull(getValue(firstMvcResult, "$.updatedBy")),
                () -> assertNull(getValue(firstMvcResult, "$.updatedDate")),
                () -> assertEquals(firstWorkspace.getName(), getValue(firstMvcResult, "$.name")),
                () -> assertNull(getValue(firstMvcResult, "$.description")),
                () -> assertEquals(WorkspaceVisibility.PRIVATE.toString(), getValue(firstMvcResult, "$.visibility")),
                () -> assertTrue(testFirstMembersId.contains(firstMember.getId())),
                () -> assertEquals(1, testFirstMembersId.size()),

                () -> assertEquals(HttpStatus.CREATED.value(), secondMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(secondMvcResult, "$.id")),
                () -> assertEquals(secondWorkspace.getCreatedBy(), getValue(secondMvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(secondMvcResult, "$.createdDate")),
                () -> assertNull(getValue(secondMvcResult, "$.updatedBy")),
                () -> assertNull(getValue(secondMvcResult, "$.updatedDate")),
                () -> assertEquals(secondWorkspace.getName(), getValue(secondMvcResult, "$.name")),
                () -> assertEquals(secondWorkspace.getDescription(),getValue(secondMvcResult, "$.description")),
                () -> assertEquals(secondWorkspace.getVisibility().toString(), getValue(secondMvcResult, "$.visibility")),
                () -> assertTrue(testSecondMembersId.contains(firstMember.getId())),
                () -> assertTrue(testSecondMembersId.contains(secondMember.getId())),
                () -> assertEquals(2, testSecondMembersId.size())
        );
    }

    @Test
    public void createFailure() throws Exception {
        User entity = new User();
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
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
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
        List<Workspace> testUsers = helper.getWorkspacesArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testUsers.contains(workspace))
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
        Workspace workspace = helper.getNewWorkspace("1update@WIT");
        Member secondMember = helper.getNewMember("2update@WIT");
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
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(workspace.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(workspace.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(workspace.getDescription(), getValue(mvcResult, "$.description")),
                () -> assertEquals(WorkspaceVisibility.PUBLIC.toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertEquals(2, membersId.size())
        );
    }
}
