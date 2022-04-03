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

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardIntegrationTest extends AbstractIntegrationTest<Board> {
    private final String URL_TEMPLATE = "/boards";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void createWithoutArg() throws Exception {
        Workspace workspace = helper.getNewWorkspace("createWithoutArg@BIT");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setName("name");
        board.setWorkspaceId(workspace.getId());
        Set<UUID> membersId = new HashSet<>();
        membersId.add(workspace.getMembersId().iterator().next());
        board.setMembersId(membersId);
        MvcResult mvcResult = super.create(URL_TEMPLATE, board);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertTrue(getValue(mvcResult, "$.createdDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(board.getName(), getValue(mvcResult, "$.name")),
                () -> assertNull(getValue(mvcResult, "$.description")),
                () -> assertEquals(board.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.favourite")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(workspace.getId().toString(), getValue(mvcResult, "$.workspaceId")),
                () -> assertTrue(testMembersId.contains(workspace.getMembersId().iterator().next())),
                () -> assertEquals(1, testMembersId.size())
        );
    }

    @Test
    public void createFromArg() throws Exception {
        Workspace workspace = helper.getNewWorkspace("createFromArg1@BIT");
        Member secondMember = helper.getNewMember("createFromArg2@BIT");
        Board board = new Board();
        board.setCreatedBy(workspace.getCreatedBy());
        board.setName("name");
        board.setDescription("description");
        board.setVisibility(BoardVisibility.PUBLIC);
        board.setFavourite(true);
        board.setArchived(true);
        board.setWorkspaceId(workspace.getId());
        Set<UUID> membersId = new HashSet<>();
        membersId.add(workspace.getMembersId().iterator().next());
        membersId.add(secondMember.getId());
        board.setMembersId(membersId);
        MvcResult mvcResult = super.create(URL_TEMPLATE, board);
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertTrue(getValue(mvcResult, "$.createdDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(board.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(board.getDescription(), getValue(mvcResult, "$.description")),
                () -> assertEquals(board.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.favourite")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(workspace.getId().toString(), getValue(mvcResult, "$.workspaceId")),
                () -> assertTrue(testMembersId.contains(membersId.iterator().next())),
                () -> assertTrue(testMembersId.contains(membersId.iterator().next())),
                () -> assertEquals(2, testMembersId.size())
        );
    }

    @Test
    public void createFailure() throws Exception {
        Board entity = new Board();
        MvcResult mvcResult = super.create(URL_TEMPLATE, entity);

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void findAll() throws Exception {
        Board firstBoard = helper.getNewBoard("1findAll@BIT");
        Board secondBoard = helper.getNewBoard("2findAll@BIT");
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
        Board board = helper.getNewBoard("findById@BIT");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, board.getId());
        Set<UUID> testMembersId = helper.getIdsFromJson(getValue(mvcResult, "$.membersId").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
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
        Board board = helper.getNewBoard("deleteById@BIT");
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

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus())
        );
    }

    @Test
    public void update() throws Exception {
        Board board = helper.getNewBoard("1update@WIT");
        Member secondMember = helper.getNewMember("2update@WIT");
        board.setUpdatedBy(board.getCreatedBy());
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
                () -> assertEquals(LocalDateTime.of(2022, 2, 2, 2, 2, 2).toString(),
                        getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(board.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertTrue(getValue(mvcResult, "$.updatedDate").toString().
                        contains(Date.valueOf(LocalDate.now()).toString())),
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
    public void updateFailure() throws Exception {
        Board firstBoard = helper.getNewBoard("1updateFailure@BoardIntegrationTest");
        firstBoard.setName(null);
        firstBoard.setUpdatedBy(firstBoard.getCreatedBy());

        Board secondBoard = new Board();
        secondBoard.setId(firstBoard.getId());

        MvcResult firstMvcResult = super.update(URL_TEMPLATE, firstBoard.getId(), firstBoard);
        MvcResult secondMvcResult = super.update(URL_TEMPLATE, secondBoard.getId(), secondBoard);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), secondMvcResult.getResponse().getStatus())
        );
    }
}
