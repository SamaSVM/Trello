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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WorkspaceIntegrationTest extends AbstractIntegrationTest<Workspace> {
    private final String URL_TEMPLATE = "/workspaces";

    private final IntegrationHelper helper;

    @Autowired
    public WorkspaceIntegrationTest(IntegrationHelper helper) {
        this.helper = helper;
    }


    @Test
    public void create() throws Exception {
        Member firstMember = helper.getNewMember("1create@WIT.com");
        Member secondMember = helper.getNewMember("2create@WIT.com");
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(firstMember.getCreatedBy());
        workspace.setCreatedDate(LocalDateTime.now().withNano(0));
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
                () -> assertEquals(workspace.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
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
    public void findAll() throws Exception {
        Workspace firstWorkspace = helper.getNewWorkspace("1findAll@WIT.com");
        Workspace secondWorkspace = helper.getNewWorkspace("2findAll@WIT.com");
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
        Workspace workspace = helper.getNewWorkspace("findById@WIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, workspace.getId());
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(workspace.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(workspace.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
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
        Workspace workspace = helper.getNewWorkspace("deleteById@WIT.com");
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
        Workspace workspace = helper.getNewWorkspace("1update@WIT.com");
        Member secondMember = helper.getNewMember("2update@WIT.com");
        workspace.setUpdatedBy(workspace.getCreatedBy());
        workspace.setUpdatedDate(LocalDateTime.now().withNano(0));
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
                () -> assertEquals(workspace.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(workspace.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(workspace.getUpdatedDate().toString(), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(workspace.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(workspace.getDescription(), getValue(mvcResult, "$.description")),
                () -> assertEquals(WorkspaceVisibility.PUBLIC.toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertEquals(2, membersId.size())
        );
    }

    @Test
    public void badFieldsCreate() throws Exception {
        Member member = helper.getNewMember("badResourceFieldsCreate@WIT.com");
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("c");
        workspace.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        workspace.setName("n");
        workspace.setDescription("d");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        membersId.add(UUID.randomUUID());
        workspace.setMembersId(membersId);
        MvcResult mvcResult = super.create(URL_TEMPLATE, workspace);

        String createdByMessage = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateMessage = "The createdDate should not be past or future. \n";
        String nameMessage = "The name field must be between 2 and 20 characters long. \n";
        String descriptionMessage = "The description field must be between 2 and 255 characters long. \n";
        String memberIdMessage = " - memberId must belong to the member. \n";
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByMessage)),
                () -> assertTrue(exceptionMessage.contains(createdDateMessage)),
                () -> assertTrue(exceptionMessage.contains(nameMessage)),
                () -> assertTrue(exceptionMessage.contains(descriptionMessage)),
                () -> assertTrue(exceptionMessage.contains(memberIdMessage))
        );
    }

    @Test
    public void nullCreatedByFieldCreate() throws Exception {
        Member member = helper.getNewMember("nullCreatedByFieldCreate@WIT.com");
        Workspace workspace = new Workspace();
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setName("name");
        workspace.setDescription("description");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        MvcResult mvcResult = super.create(URL_TEMPLATE, workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldCreate() throws Exception {
        Member member = helper.getNewMember("nullCreatedDateFieldCreate@WIT.com");
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setName("name");
        workspace.setDescription("description");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        MvcResult mvcResult = super.create(URL_TEMPLATE, workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameFieldCreate() throws Exception {
        Member member = helper.getNewMember("nullNameFieldCreate@WIT.com");
        Workspace workspace = new Workspace();
        workspace.setCreatedBy(member.getCreatedBy());
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setDescription("description");
        Set<UUID> membersId = new HashSet<>();
        membersId.add(member.getId());
        workspace.setMembersId(membersId);
        MvcResult mvcResult = super.create(URL_TEMPLATE, workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badMemberIdFieldsCreate() throws Exception {
        Workspace workspace = new Workspace();
        workspace.setCreatedBy("createdBy");
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setName("name");
        workspace.setDescription("description");
        workspace.setMembersId(new HashSet<>());
        MvcResult mvcResult = super.create(URL_TEMPLATE, workspace);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The resource must belong to at least one member! \n",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badFieldsUpdate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("badFieldsUpdate@WIT.com");
        workspace.setCreatedBy("newCreatedBy");
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setUpdatedBy("u");
        workspace.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        workspace.setName("n");
        workspace.setDescription("d");
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(UUID.randomUUID());
        workspace.setMembersId(membersId);
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);

        String createdByMessage = "The createdBy field cannot be updated. \n";
        String createdDateMessage = "The createdDate field cannot be updated. \n";
        String updatedByMessage = "UpdatedBy should be between 2 and 30 characters! \n";
        String updatedDateMessage = "The updatedDate should not be past or future. \n";
        String nameMessage = "The name field must be between 2 and 20 characters long. \n";
        String descriptionMessage = "The description field must be between 2 and 255 characters long. \n";
        String memberIdMessage = " - memberId must belong to the member. \n";
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByMessage)),
                () -> assertTrue(exceptionMessage.contains(createdDateMessage)),
                () -> assertTrue(exceptionMessage.contains(nameMessage)),
                () -> assertTrue(exceptionMessage.contains(descriptionMessage)),
                () -> assertTrue(exceptionMessage.contains(memberIdMessage)),
                () -> assertTrue(exceptionMessage.contains(updatedByMessage)),
                () -> assertTrue(exceptionMessage.contains(updatedDateMessage))
        );
    }

    @Test
    public void nullCreatedByFieldUpdate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullCreatedByFieldUpdate@WIT.com");
        workspace.setCreatedBy(null);
        workspace.setCreatedDate(LocalDateTime.now());
        workspace.setUpdatedBy("updatedBy");
        workspace.setUpdatedDate(LocalDateTime.now());
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldUpdate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullCreatedDateFieldUpdate@WIT.com");
        workspace.setCreatedDate(null);
        workspace.setUpdatedBy("updatedBy");
        workspace.setUpdatedDate(LocalDateTime.now());
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nonExistentWorkspaceUpdate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nonExistentWorkspaceUpdate@WIT.com");
        workspace.setId(UUID.randomUUID());
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent workspace!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByFieldUpdate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullUpdatedByFieldUpdate@WIT.com");
        workspace.setUpdatedDate(LocalDateTime.now());
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateFieldUpdate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullUpdatedDateFieldUpdate@WIT.com");
        workspace.setUpdatedBy("updatedBy");
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullMemberUpdate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullMemberUpdate@WIT.com");
        workspace.setUpdatedDate(LocalDateTime.now());
        workspace.setUpdatedBy("UpdatedBy");
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(UUID.randomUUID());
        workspace.setMembersId(membersId);
        MvcResult mvcResult = super.update(URL_TEMPLATE, workspace.getId(), workspace);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()
                        .contains("- memberId must belong to the member."))
        );
    }
}
