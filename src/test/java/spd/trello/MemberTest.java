package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.MemberRepository;
import spd.trello.services.MemberService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static spd.trello.Helper.*;
import static spd.trello.Helper.deleteUser;

public class MemberTest extends BaseTest {
    private final MemberService service;

    public MemberTest() {
        service = new MemberService(new MemberRepository(dataSource));
    }

    @Test
    public void successCreate() {
        User user = getNewUser();
        Member testMember = service.create(user, MemberRole.MEMBER);
        assertNotNull(testMember);
        assertAll(
                () -> assertEquals("test@mail", testMember.getCreatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getCreatedDate()),
                () -> assertEquals(MemberRole.MEMBER, testMember.getMemberRole()),
                () -> assertEquals(user.getId(), testMember.getUserId())
        );
        service.delete(testMember.getId());
        deleteUser(user.getId());
    }

    @Test
    public void createFailure() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> service.create(null, MemberRole.ADMIN),
                "expected to throw INullPointException, but it didn't"
        );
        assertEquals("Cannot invoke \"spd.trello.domain.User.getEmail()\" because \"user\" is null", ex.getMessage());
    }

    @Test
    public void testFindById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Member with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void testDelete() {
        User user = getNewUser();
        Member testMember = service.create(user, MemberRole.MEMBER);
        assertNotNull(testMember);
        UUID id = testMember.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
        deleteUser(user.getId());
    }

    @Test
    public void testUpdate() {
        User user = getNewUser();
        Member testMember = service.create(user, MemberRole.MEMBER);
        assertNotNull(testMember);
        testMember.setMemberRole(MemberRole.ADMIN);
        UUID id = service.update(user, testMember).getId();
        assertAll(
                () -> assertEquals("test@mail", service.findById(id).getCreatedBy()),
                () -> assertEquals("test@mail", service.findById(id).getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), service.findById(id).getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), service.findById(id).getUpdatedDate()),
                () -> assertEquals(MemberRole.ADMIN, service.findById(id).getMemberRole()),
                () -> assertEquals(user.getId(), service.findById(id).getUserId())
        );
        service.delete(testMember.getId());
        deleteUser(user.getId());
    }

    @Test
    public void updateFailure() {
        User user = new User();
        Member testMember = new Member();
        testMember.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(user, testMember),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Member with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
