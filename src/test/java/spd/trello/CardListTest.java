package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.*;
import spd.trello.domain.enums.BoardVisibility;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.BoardRepository;
import spd.trello.repository.CardListRepository;
import spd.trello.services.BoardService;
import spd.trello.services.CardListService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static spd.trello.Helper.*;
import static spd.trello.Helper.deleteUser;

public class CardListTest extends BaseTest{
    private final CardListService service;

    public CardListTest() {
        service = new CardListService(new CardListRepository(dataSource));
    }

    @Test
    public void successCreate() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList testCardList = service.create(member, board.getId(), "testCardList");
        assertNotNull(testCardList);
        assertAll(
                () -> assertEquals("test@mail", testCardList.getCreatedBy()),
                () -> assertNull(testCardList.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCardList.getCreatedDate()),
                () -> assertNull(testCardList.getUpdatedDate()),
                () -> assertEquals("testCardList", testCardList.getName()),
                () -> assertFalse(testCardList.getArchived()),
                () -> assertEquals(board.getId(), testCardList.getBoardId())
        );
        service.delete(testCardList.getId());
        deleteBoard(board.getId());
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void testFindAll() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList testFirstCardList = service.create(member, board.getId(), "1CardList");
        CardList testSecondCardList = service.create(member, board.getId(), "2CardList");
        assertNotNull(testFirstCardList);
        assertNotNull(testSecondCardList);
        List<CardList> testCardLists = service.findAll();
        assertAll(
                () -> assertTrue(testCardLists.contains(testFirstCardList)),
                () -> assertTrue(testCardLists.contains(testSecondCardList))
        );
        for (CardList cardList : testCardLists) {
            service.delete(cardList.getId());
        }
        deleteBoard(board.getId());
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void createFailure() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member, null, "Name"),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("CardList doesn't creates", ex.getMessage());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void testFindById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("CardList with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void testDelete() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList testCardList = service.create(member,board.getId(), "testCardList");
        assertNotNull(testCardList);
        UUID id = testCardList.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
        deleteBoard(board.getId());
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void testUpdate() {
        User user = getNewUser("test@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = service.create(member, board.getId(), "name");
        assertNotNull(cardList);
        cardList.setName("newCardList");
        cardList.setArchived(true);
        CardList testCardList = service.update(member, cardList);
        assertAll(
                () -> assertEquals("test@mail", testCardList.getCreatedBy()),
                () -> assertEquals("test@mail", testCardList.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCardList.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testCardList.getUpdatedDate()),
                () -> assertEquals("newCardList", testCardList.getName()),
                () -> assertTrue(testCardList.getArchived()),
                () -> assertEquals(board.getId(), testCardList.getBoardId())
        );
        service.delete(testCardList.getId());
        deleteBoard(board.getId());
        deleteWorkspace(workspace.getId());
        deleteMember(member.getId());
        deleteUser(user.getId());
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        CardList testCardList = new CardList();
        testCardList.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testCardList),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("CardList with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
