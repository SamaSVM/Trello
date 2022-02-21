package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Board;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.Workspace;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.domain.enums.WorkspaceVisibility;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BoardIntegrationTest extends AbstractIntegrationTest<User>{
    private final String URL_TEMPLATE = "/boards";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        Workspace workspace = helper.getNewWorkspace("1create@BIT");
        Board firstBoard = new Board();
        firstBoard.setCreatedBy(workspace.getCreatedBy());
        firstBoard.setName("1name");
        firstBoard.setWorkspaceId(workspace.getId());
        Set<UUID> membersIds = new HashSet<>();
        membersIds.add(workspace.getMembersIds().iterator().next());
        firstBoard.setMembersIds(membersIds);
        MvcResult firstMvcResult = super.create(URL_TEMPLATE, firstBoard);
        Set<UUID> testFirstMembersIds = helper.getIdsFromJson(getValue(firstMvcResult, "$.membersIds").toString());

        Member secondMember = helper.getNewMember("2create@BIT");
        Board secondBoard = new Board();
        secondBoard.setCreatedBy(workspace.getCreatedBy());
        secondBoard.setName("2name");
        secondBoard.setDescription("2description");
        secondBoard.setVisibility(BoardVisibility.PUBLIC);
        secondBoard.setFavourite(true);
        secondBoard.setArchived(true);
        secondBoard.setWorkspaceId(workspace.getId());
        membersIds.add(secondMember.getId());
        secondBoard.setMembersIds(membersIds);
        MvcResult secondMvcResult = super.create(URL_TEMPLATE, secondBoard);
        Set<UUID> testSecondMembersIds = helper.getIdsFromJson(getValue(secondMvcResult, "$.membersIds").toString());

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(firstMvcResult, "$.id")),
                () -> assertEquals(firstBoard.getCreatedBy(), getValue(firstMvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(firstMvcResult, "$.createdDate")),
                () -> assertNull(getValue(firstMvcResult, "$.updatedBy")),
                () -> assertNull(getValue(firstMvcResult, "$.updatedDate")),
                () -> assertEquals(firstBoard.getName(), getValue(firstMvcResult, "$.name")),
                () -> assertNull(getValue(firstMvcResult, "$.description")),
                () -> assertEquals(firstBoard.getVisibility().toString(), getValue(firstMvcResult, "$.visibility")),
                () -> assertFalse((Boolean) getValue(firstMvcResult, "$.favourite")),
                () -> assertFalse((Boolean) getValue(firstMvcResult, "$.archived")),
                () -> assertEquals(workspace.getId().toString(), getValue(firstMvcResult, "$.workspaceId")),
                () -> assertTrue(testFirstMembersIds.contains(workspace.getMembersIds().iterator().next())),
                () -> assertEquals(1, testFirstMembersIds.size()),

                () -> assertEquals(HttpStatus.CREATED.value(), secondMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(secondMvcResult, "$.id")),
                () -> assertEquals(secondBoard.getCreatedBy(), getValue(secondMvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(secondMvcResult, "$.createdDate")),
                () -> assertNull(getValue(secondMvcResult, "$.updatedBy")),
                () -> assertNull(getValue(secondMvcResult, "$.updatedDate")),
                () -> assertEquals(secondBoard.getName(), getValue(secondMvcResult, "$.name")),
                () -> assertEquals(secondBoard.getDescription(),getValue(secondMvcResult, "$.description")),
                () -> assertEquals(secondBoard.getVisibility().toString(), getValue(secondMvcResult, "$.visibility")),
                () -> assertTrue((Boolean) getValue(secondMvcResult, "$.favourite")),
                () -> assertTrue((Boolean) getValue(secondMvcResult, "$.archived")),
                () -> assertEquals(workspace.getId().toString(), getValue(secondMvcResult, "$.workspaceId")),
                () -> assertTrue(testSecondMembersIds.contains(membersIds.iterator().next())),
                () -> assertTrue(testSecondMembersIds.contains(membersIds.iterator().next())),
                () -> assertEquals(2, testSecondMembersIds.size())
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
        Set<UUID> testMembersIds = helper.getIdsFromJson(getValue(mvcResult, "$.membersIds").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(board.getName(), getValue(mvcResult, "$.name")),
                () -> assertNull(getValue(mvcResult, "$.description")),
                () -> assertEquals(board.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.favourite")),
                () -> assertFalse((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(board.getWorkspaceId().toString(), getValue(mvcResult, "$.workspaceId")),
                () -> assertTrue(testMembersIds.contains(board.getMembersIds().iterator().next())),
                () -> assertEquals(1, testMembersIds.size())
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
        List<Board> testUsers = helper.getBoardsArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testUsers.contains(board))
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
        Set<UUID> membersIds = board.getMembersIds();
        membersIds.add(secondMember.getId());
        board.setMembersIds(membersIds);
        MvcResult mvcResult = super.update(URL_TEMPLATE, board.getId(), board);
        Set<UUID> testMembersIds = helper.getIdsFromJson(getValue(mvcResult, "$.membersIds").toString());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(board.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(board.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()),getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(board.getName(), getValue(mvcResult, "$.name")),
                () -> assertEquals(board.getDescription(),getValue(mvcResult, "$.description")),
                () -> assertEquals(board.getVisibility().toString(), getValue(mvcResult, "$.visibility")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.favourite")),
                () -> assertTrue((Boolean) getValue(mvcResult, "$.archived")),
                () -> assertEquals(board.getWorkspaceId().toString(), getValue(mvcResult, "$.workspaceId")),
                () -> assertTrue(testMembersIds.contains(membersIds.iterator().next())),
                () -> assertTrue(testMembersIds.contains(membersIds.iterator().next())),
                () -> assertEquals(2, testMembersIds.size())
        );
    }
}
