package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.BoardVisibility;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardIntegrationTest extends AbstractIntegrationTest<Board> {
    private final String URL_TEMPLATE = "/boards";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Workspace workspace = helper.getNewWorkspace("create1@BIT.com");
        Member secondMember = helper.getNewMember("create2@BIT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setVisibility(BoardVisibility.PUBLIC);
        board.setWorkspaceId(workspace.getId());
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(secondMember.getId());
        board.setMembersId(membersId);
        MvcResult mvcResult = super.create(URL_TEMPLATE, board);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(board.getCreatedDate().toString(),
                        getValue(mvcResult, "$.createdDate").toString()),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(board.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(board.getDescription(), getValue(mvcResult, "$.description")),
                () -> assertEquals(board.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.favourite")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(workspace.getId().toString(), getValue(mvcResult, "$.workspaceId")),
                () -> assertTrue(testMembersId.contains(membersId.iterator().next())),
                () -> assertTrue(testMembersId.contains(membersId.iterator().next())),
                () -> assertEquals(2, testMembersId.size())
        );
    }

    @Test
    public void findAll() throws Exception {
        Board firstBoard = helper.getNewBoard("1findAll@BIT.com");
        Board secondBoard = helper.getNewBoard("2findAll@BIT.com");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Board> testWorkspaces = helper.getBoardsArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testWorkspaces.contains(firstBoard)),
                () -> assertTrue(testWorkspaces.contains(secondBoard))
        );
    }

    @Test
    public void findById() throws Exception {
        Board board = helper.getNewBoard("findById@BIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, board.getId());
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(board.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(board.getName(), getValue(mvcResult, "$.name")),
                () -> assertNull(getValue(mvcResult, "$.description")),
                () -> assertEquals(board.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.favourite")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(board.getWorkspaceId().toString(), getValue(mvcResult, "$.workspaceId")),
                () -> assertTrue(testMembersId.contains(board.getMembersId().iterator().next())),
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
        Board board = helper.getNewBoard("deleteById@BIT.com");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, board.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Board> testBoards = helper.getBoardsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testBoards.contains(board))
        );
    }

    @Test
    public void deleteByIdFailure() throws Exception {
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void update() throws Exception {
        Board board = helper.getNewBoard("1update@BIT.com");
        Member secondMember = helper.getNewMember("2update@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now().withNano(0));
        board.setName("new name");
        board.setDescription("new description");
        board.setVisibility(BoardVisibility.PUBLIC);
        board.setArchived(true);
        board.setFavourite(true);
        Set<UUID> membersId = board.getMembersId();
        membersId.add(secondMember.getId());
        board.setMembersId(membersId);
        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(board.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(board.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(board.getUpdatedDate().toString(),
                        getValue(mvcResult, "$.updatedDate").toString()),
                () -> assertEquals(board.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(board.getDescription(), getValue(mvcResult, "$.description")),
                () -> assertEquals(board.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.favourite")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(board.getWorkspaceId().toString(), getValue(mvcResult, "$.workspaceId")),
                () -> assertTrue(testMembersId.contains(membersId.iterator().next())),
                () -> assertTrue(testMembersId.contains(membersId.iterator().next())),
                () -> assertEquals(2, testMembersId.size())
        );
    }

    @Test
    public void badFieldsCreate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("badFieldsCreate@BIT.com");
        Board board = new Board();
        board.setCreatedBy("c");
        board.setCreatedDate(LocalDateTime.now().minusMinutes(2));
        board.setName("n");
        board.setDescription("d");
        board.setWorkspaceId(workspace.getId());
        Set<UUID> membersId = workspace.getMembersId();
        membersId.add(UUID.randomUUID());
        board.setMembersId(membersId);

        String createdByException = "CreatedBy should be between 2 and 30 characters! \n";
        String createdDateException = "The createdDate should not be past or future. \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String descriptionException = "The description field must be between 2 and 255 characters long. \n";
        String memberIdException = "- memberId must belong to the member.";

        MvcResult mvcResult = super.create(URL_TEMPLATE, board);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(descriptionException)),
                () -> assertTrue(exceptionMessage.contains(memberIdException))
        );
    }

    @Test
    public void archivedBoardCreate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("archivedBoardC@BIT.com");
        Board board = new Board();
        board.setArchived(true);
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("You cannot create an archived board.",
                        mvcResult.getResolvedException().getMessage())
        );
    }

    @Test
    public void badWorkspaceIdCreate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("badWorkspaceIdCreate@BIT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(UUID.randomUUID());
        board.setMembersId(workspace.getMembersId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, board);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("WorkspaceId must be owned by Workspace.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldCreate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("archivedBoardCreate@BIT.com");
        Board board = new Board();
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldCreate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullCreatedDateFieldCreate@BIT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameFieldCreate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullNameFieldCreate@BIT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(workspace.getMembersId());

        MvcResult mvcResult = super.create(URL_TEMPLATE, board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        mvcResult.getResolvedException().getMessage())
        );
    }

    @Test
    public void nullMembersCreate() throws Exception {
        Workspace workspace = helper.getNewWorkspace("nullMembersCreate@BIT.com");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setCreatedDate(LocalDateTime.now().withNano(0));
        board.setName("name");
        board.setDescription("description");
        board.setWorkspaceId(workspace.getId());
        board.setMembersId(new HashSet<>());

        MvcResult mvcResult = super.create(URL_TEMPLATE, board);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The resource must belong to at least one member!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badFieldsUpdate() throws Exception {
        Board board = helper.getNewBoard("badFieldsUpdate@BIT.com");
        board.setUpdatedDate(LocalDateTime.now().minusMinutes(2));
        board.setCreatedBy("newCreateBy");
        board.setCreatedDate(LocalDateTime.now());
        board.setUpdatedBy("u");
        board.setName("n");
        board.setDescription("d");
        board.setWorkspaceId(UUID.randomUUID());
        Set<UUID> memberId = board.getMembersId();
        memberId.add(UUID.randomUUID());

        String updatedDateException = "The updatedDate should not be past or future. \n";
        String createdByException = "The createdBy field cannot be updated. \n";
        String createdDateException = "The createdDate field cannot be updated. \n";
        String updatedByException = "UpdatedBy should be between 2 and 30 characters! \n";
        String nameException = "The name field must be between 2 and 20 characters long. \n";
        String descriptionException = "The description field must be between 2 and 255 characters long. \n";
        String transferException = "Board cannot be transferred to another workspace. \n";
        String memberIdException = " - memberId must belong to the member. \n";

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(updatedDateException)),
                () -> assertTrue(exceptionMessage.contains(createdByException)),
                () -> assertTrue(exceptionMessage.contains(createdDateException)),
                () -> assertTrue(exceptionMessage.contains(updatedByException)),
                () -> assertTrue(exceptionMessage.contains(nameException)),
                () -> assertTrue(exceptionMessage.contains(descriptionException)),
                () -> assertTrue(exceptionMessage.contains(transferException)),
                () -> assertTrue(exceptionMessage.contains(memberIdException))
        );
    }

    @Test
    public void nonExistentWorkspaceUpdate() throws Exception {
        Board board = helper.getNewBoard("nonExistentWU@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setId(UUID.randomUUID());

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent board!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void archivedBoardUpdate() throws Exception {
        Board board = helper.getNewBoard("nonExistentWorkspaceU@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setArchived(true);
        super.update(URL_TEMPLATE, board.getId(), board);
        board.setName("newName");

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("Archived board cannot be updated.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedByFieldUpdate() throws Exception {
        Board board = helper.getNewBoard("nullCreatedByFieldUpdate@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setCreatedBy(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullCreatedDateFieldUpdate() throws Exception {
        Board board = helper.getNewBoard("nullCreatedDateFieldUpdate@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setCreatedDate(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The createdBy, createdDate fields must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedByFieldUpdate() throws Exception {
        Board board = helper.getNewBoard("nullUpdatedByFieldU@BIT.com");
        board.setUpdatedDate(LocalDateTime.now());

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullUpdatedDateFieldUpdate() throws Exception {
        Board board = helper.getNewBoard("nullUpdatedDateFieldU@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullNameFieldUpdate() throws Exception {
        Board board = helper.getNewBoard("nullNameFieldUpdate@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setName(null);

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The name field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void nullMembersUpdate() throws Exception {
        Board board = helper.getNewBoard("nullMembersUpdate@BIT.com");
        board.setUpdatedBy(board.getCreatedBy());
        board.setUpdatedDate(LocalDateTime.now());
        board.setMembersId(new HashSet<>());

        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The resource must belong to at least one member!",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }
}
