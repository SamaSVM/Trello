package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.services.BoardService;
import spd.trello.services.CardService;
import spd.trello.services.MemberService;
import spd.trello.services.WorkspaceService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MemberTest {
    @Autowired
    private MemberService service;
    @Autowired
    private WorkspaceService workspaceService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CardService cardService;
    @Autowired
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test@mail");
        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member testMember = service.create(member);
        assertNotNull(testMember);
        assertAll(
                () -> assertEquals("test@mail", testMember.getCreatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getCreatedDate()),
                () -> assertEquals(MemberRole.MEMBER, testMember.getMemberRole()),
                () -> assertEquals(user.getId(), testMember.getUserId())
        );
    }

    @Test
    public void findAll() {
        User user = helper.getNewUser("test1@mail");
        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member testFirstMember = service.create(member);
        member.setMemberRole(MemberRole.ADMIN);
        Member testSecondMember = service.create(member);
        assertNotNull(testFirstMember);
        assertNotNull(testSecondMember);
        List<Member> testMembers = service.findAll();
        assertAll(
                () -> assertTrue(testMembers.contains(testFirstMember)),
                () -> assertTrue(testMembers.contains(testSecondMember))
        );
    }

    @Test
    public void createFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.MEMBER);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member),
                "expected to throw INullPointException, but it didn't"
        );
        assertEquals("Member doesn't creates", ex.getMessage());
    }

    @Test
    public void findById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Member with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test2@mail");
        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member testMember = service.create(member);
        assertNotNull(testMember);
        UUID id = testMember.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test3@mail");
        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member memberForUpdate = service.create(member);
        assertNotNull(memberForUpdate);
        memberForUpdate.setMemberRole(MemberRole.ADMIN);
        memberForUpdate.setUpdatedBy(user.getEmail());
        Member testMember = service.update(memberForUpdate);
        assertNotNull(testMember);
        assertAll(
                () -> assertEquals("test3@mail", testMember.getCreatedBy()),
                () -> assertEquals("test3@mail", testMember.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getUpdatedDate()),
                () -> assertEquals(MemberRole.ADMIN, testMember.getMemberRole()),
                () -> assertEquals(user.getId(), testMember.getUserId())
        );
    }

    @Test
    public void updateFailure() {
        Member testMember = new Member();
        testMember.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testMember),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Member with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
