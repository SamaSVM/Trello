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
        User testUser = getNewUser();
        Member testMember = service.create(testUser, MemberRole.MEMBER);
        assertNotNull(testMember);
        assertAll(
                () -> assertEquals("test@mail", testMember.getCreatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getCreatedDate()),
                () -> assertEquals(MemberRole.MEMBER, testMember.getMemberRole()),
                () -> assertEquals(testUser.getId(), testMember.getUserId())
        );
        service.delete(testMember.getId());
        deleteUser(testUser.getId());
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
        User testUser = getNewUser();
        Member testMember = service.create(testUser, MemberRole.MEMBER);
        assertNotNull(testMember);
        UUID id = testMember.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
        deleteUser(testUser.getId());
    }

    @Test
    public void testUpdate() {
        User testUser = getNewUser();
        Member testMember = service.create(testUser, MemberRole.MEMBER);
        assertNotNull(testMember);
        testMember.setMemberRole(MemberRole.ADMIN);
        UUID id = service.update(testUser, testMember).getId();
        assertAll(
                () -> assertEquals("test@mail", service.findById(id).getCreatedBy()),
                () -> assertEquals("test@mail", service.findById(id).getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), service.findById(id).getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), service.findById(id).getUpdatedDate()),
                () -> assertEquals(MemberRole.ADMIN, service.findById(id).getMemberRole()),
                () -> assertEquals(testUser.getId(), service.findById(id).getUserId())
        );
        service.delete(testMember.getId());
        deleteUser(testUser.getId());
    }
}
