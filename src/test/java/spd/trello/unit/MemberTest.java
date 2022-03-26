package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.MemberService;

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
    private UnitHelper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test@MT");

        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member testMember = service.save(member);

        assertNotNull(testMember);
        assertAll(
                () -> assertEquals(user.getEmail(), testMember.getCreatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getCreatedDate()),
                () -> assertEquals(MemberRole.MEMBER, testMember.getMemberRole()),
                () -> assertEquals(user.getId(), testMember.getUserId())
        );
    }

    @Test
    public void findAll() {
        Member firstMember = helper.getNewMember("1findAll@MT");
        Member secondMember = helper.getNewMember("2findAll@MT");

        assertNotNull(firstMember);
        assertNotNull(secondMember);
        List<Member> testMembers = service.getAll();
        assertAll(
                () -> assertTrue(testMembers.contains(firstMember)),
                () -> assertTrue(testMembers.contains(secondMember))
        );
    }

    @Test
    public void findById() {
        Member member = helper.getNewMember("findById@MT");

        Member testMember = service.getById(member.getId());
        assertEquals(member, testMember);
    }

    @Test
    public void delete() {
        Member member = helper.getNewMember("delete@MT");

        assertNotNull(member);
        service.delete(member.getId());
        assertFalse(service.getAll().contains(member));
    }

    @Test
    public void update() {
        Member member = helper.getNewMember("update@MT");

        assertNotNull(member);
        member.setMemberRole(MemberRole.MEMBER);
        member.setUpdatedBy("newMember");
        Member testMember = service.update(member);

        assertNotNull(testMember);
        assertAll(
                () -> assertEquals(member.getCreatedBy(), testMember.getCreatedBy()),
                () -> assertEquals(member.getUpdatedBy(), testMember.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getUpdatedDate()),
                () -> assertEquals(MemberRole.MEMBER, testMember.getMemberRole()),
                () -> assertEquals(member.getUserId(), testMember.getUserId())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new Member()),
                "no exception"
        );
        assertTrue(ex.getMessage().contains("could not execute statement;"));
    }

    @Test
    public void findByIdFailure() {
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.getById(UUID.randomUUID()),
                "no exception"
        );
        assertEquals("Resource not found Exception!", ex.getMessage());
    }

    @Test
    public void deleteFailure() {
        UUID id = UUID.randomUUID();
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.delete(id),
                "no exception"
        );
        assertEquals("No class spd.trello.domain.Member entity with id " + id + " exists!", ex.getMessage());
    }
}
