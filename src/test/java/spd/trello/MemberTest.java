package spd.trello;

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
    private Helper helper;

    @Test
    public void successCreate() {
        User user = helper.getNewUser("test@mail");

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
        User user = helper.getNewUser("test1@mail");

        Member firstMember = new Member();
        firstMember.setCreatedBy(user.getEmail());
        firstMember.setUserId(user.getId());
        firstMember.setMemberRole(MemberRole.MEMBER);
        Member testFirstMember = service.save(firstMember);

        Member secondMember = new Member();
        secondMember.setCreatedBy(user.getEmail());
        secondMember.setUserId(user.getId());
        secondMember.setMemberRole(MemberRole.ADMIN);
        Member testSecondMember = service.save(secondMember);

        assertNotNull(testFirstMember);
        assertNotNull(testSecondMember);
        List<Member> testMembers = service.getAll();
        assertAll(
                () -> assertTrue(testMembers.contains(testFirstMember)),
                () -> assertTrue(testMembers.contains(testSecondMember))
        );
    }

    @Test
    public void findById() {
        User user = helper.getNewUser("findById@MT");

        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        service.save(member);

        Member testMember = service.getById(member.getId());
        assertEquals(member, testMember);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("test2@mail");

        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member testMember = service.save(member);

        assertNotNull(testMember);
        service.delete(testMember.getId());
        assertFalse(service.getAll().contains(testMember));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("test3@mail");

        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member memberForUpdate = service.save(member);

        assertNotNull(memberForUpdate);
        memberForUpdate.setUpdatedBy(user.getEmail());
        memberForUpdate.setMemberRole(MemberRole.ADMIN);
        memberForUpdate.setUpdatedBy(user.getEmail());
        Member testMember = service.update(memberForUpdate);

        assertNotNull(testMember);
        assertAll(
                () -> assertEquals(user.getEmail(), testMember.getCreatedBy()),
                () -> assertEquals(user.getEmail(), testMember.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testMember.getUpdatedDate()),
                () -> assertEquals(MemberRole.ADMIN, testMember.getMemberRole()),
                () -> assertEquals(user.getId(), testMember.getUserId())
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
