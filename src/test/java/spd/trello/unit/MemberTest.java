package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.enums.MemberRole;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.MemberService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        User user = helper.getNewUser("test@MT.com");

        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setCreatedDate(LocalDateTime.now());
        member.setUserId(user.getId());
        member.setMemberRole(MemberRole.MEMBER);
        Member testMember = service.save(member);

        assertNotNull(testMember);
        assertAll(
                () -> assertEquals(user.getEmail(), testMember.getCreatedBy()),
                () -> assertTrue(testMember.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals(MemberRole.MEMBER, testMember.getMemberRole()),
                () -> assertEquals(user.getId(), testMember.getUserId())
        );
    }

    @Test
    public void findAll() {
        Member firstMember = helper.getNewMember("1findAll@MT.com");
        Member secondMember = helper.getNewMember("2findAll@MT.com");

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
        Member member = helper.getNewMember("findById@MT.com");

        Member testMember = service.getById(member.getId());
        assertEquals(member, testMember);
    }

    @Test
    public void delete() {
        Member member = helper.getNewMember("delete@MT.com");

        assertNotNull(member);
        service.delete(member.getId());
        assertFalse(service.getAll().contains(member));
    }

    @Test
    public void update() {
        Member member = helper.getNewMember("update@MT.com");
        assertNotNull(member);
        member.setMemberRole(MemberRole.MEMBER);
        member.setUpdatedBy(member.getCreatedBy());
        member.setUpdatedDate(LocalDateTime.now().withNano(0));
        Member testMember = service.update(member);

        assertNotNull(testMember);
        assertAll(
                () -> assertEquals(member.getCreatedBy(), testMember.getCreatedBy()),
                () -> assertEquals(member.getUpdatedBy(), testMember.getUpdatedBy()),
                () -> assertTrue(testMember.getCreatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertTrue(testMember.getUpdatedDate().toString()
                        .contains(Date.valueOf(LocalDate.now()).toString())),
                () -> assertEquals(MemberRole.MEMBER, testMember.getMemberRole()),
                () -> assertEquals(member.getUserId(), testMember.getUserId())
        );
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

    @Test
    public void validationCreate() {
        User user = helper.getNewUser("validationCreate@MT.com");
        Member member = new Member();
        member.setCreatedBy(user.getEmail());
        member.setCreatedDate(LocalDateTime.now().minusMinutes(2L));
        member.setUserId(UUID.randomUUID());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(member), "no exception"
        );
        assertTrue(ex.getMessage().contains("The createdDate should not be past or future. \n" +
                "The userId field must belong to a user."));
    }

    @Test
    public void nonExistentMemberUpdate() {
        Member member = helper.getNewMember("nonExistentMember@MT.com");
        member.setId(UUID.randomUUID());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.update(member), "no exception"
        );
        assertEquals("Cannot update non-existent member!", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
        Member member = helper.getNewMember("validationUpdate@MT.com");
        member.setUserId(UUID.randomUUID());
        member.setUpdatedBy(member.getCreatedBy());
        member.setUpdatedDate(LocalDateTime.now().minusMinutes(2L));
        member.setCreatedBy("newCreatedBy");
        member.setCreatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(member), "no exception"
        );
        assertTrue(ex.getMessage().contains("The updatedDate should not be past or future. \n" +
                "The createdBy field cannot be updated. \n" +
                "The createdDate field cannot be updated. \n" +
                "Member cannot be transferred to another user. "));
    }

    @Test
    public void nullUpdatedByFieldUpdate() {
        Member member = helper.getNewMember("nullUpdatedByField@MT.com");
        member.setUpdatedDate(LocalDateTime.now());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(member), "no exception"
        );
        assertEquals("The updatedBy field must be filled.", ex.getMessage());
    }

    @Test
    public void nullUpdatedDateFieldUpdate() {
        Member member = helper.getNewMember("nullUpdatedDateFieldUpdate@MT.com");
        member.setUpdatedBy(member.getCreatedBy());

        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(member), "no exception"
        );
        assertEquals("The updatedDate field must be filled.", ex.getMessage());
    }
}
