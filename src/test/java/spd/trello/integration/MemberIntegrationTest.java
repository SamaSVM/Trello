package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.enums.MemberRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MemberIntegrationTest extends AbstractIntegrationTest<Member> {
    private final String URL_TEMPLATE = "/members";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        User user = helper.getNewUser("create@MIT.com");
        Member member = new Member();
        member.setUserId(user.getId());
        member.setCreatedBy(user.getEmail());
        member.setCreatedDate(LocalDateTime.now().withNano(0));
        member.setMemberRole(MemberRole.ADMIN);
        MvcResult mvcResult = super.create(URL_TEMPLATE, member);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(member.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(member.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(user.getId().toString(), getValue(mvcResult, "$.userId")),
                () -> assertEquals(member.getMemberRole().toString(), getValue(mvcResult, "$.memberRole"))
        );
    }

    @Test
    public void findAll() throws Exception {
        Member firstMember = helper.getNewMember("1findAll@MIT.com");
        Member secondMember = helper.getNewMember("2findAll@MIT.com");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<Member> testMembers = helper.getMembersArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testMembers.contains(firstMember)),
                () -> assertTrue(testMembers.contains(secondMember))
        );
    }

    @Test
    public void findById() throws Exception {
        Member member = helper.getNewMember("findById@MIT.com");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, member.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(member.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(member.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertNull(getValue(mvcResult, "$.updatedBy")),
                () -> assertNull(getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(member.getUserId().toString(), getValue(mvcResult, "$.userId")),
                () -> assertEquals(member.getMemberRole().toString(), getValue(mvcResult, "$.memberRole"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        Member member = helper.getNewMember("deleteById@MIT.com");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, member.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Member> testMembers = helper.getMembersArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testMembers.contains(member))
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
        Member member = helper.getNewMember("update@MIT.com");
        member.setUpdatedBy(member.getCreatedBy());
        member.setUpdatedDate(LocalDateTime.now().withNano(0));
        member.setMemberRole(MemberRole.ADMIN);
        MvcResult mvcResult = super.update(URL_TEMPLATE, member.getId(), member);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(member.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(member.getCreatedDate().toString(), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(member.getUpdatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(member.getUpdatedDate().toString(), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(member.getUserId().toString(), getValue(mvcResult, "$.userId")),
                () -> assertEquals(member.getMemberRole().toString(), getValue(mvcResult, "$.memberRole"))
        );
    }

    @Test
    public void nullFieldsCreate() throws Exception {
        Member member = new Member();
        MvcResult mvcResult = super.create(URL_TEMPLATE, member);

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus()),
                () -> assertEquals("The userId field must be filled.",
                        Objects.requireNonNull(mvcResult.getResolvedException()).getMessage())
        );
    }

    @Test
    public void badRequestCreate() throws Exception {
        Member member = new Member();
        member.setCreatedBy("badRequestCreate@MIT.com");
        member.setCreatedDate(LocalDateTime.now().minusMinutes(2L).withNano(0));
        member.setUserId(UUID.randomUUID());

        MvcResult mvcResult = super.create(URL_TEMPLATE, member);
        String createdDateMessage = "The createdDate should not be past or future.";
        String userIdMessage = "The userId field must belong to a user.";

        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdDateMessage)),
                () -> assertTrue(exceptionMessage.contains(userIdMessage))
        );
    }

    @Test
    public void badFieldsUpdate() throws Exception {
        Member member = helper.getNewMember("badFieldsUpdate@MIT.com");
        Member testMember = new Member();
        testMember.setCreatedBy("c");
        testMember.setCreatedDate(LocalDateTime.now());
        testMember.setUpdatedBy(member.getCreatedBy());
        testMember.setUpdatedDate(LocalDateTime.now());
        testMember.setUserId(member.getUserId());
        MvcResult mvcResult = super.update(URL_TEMPLATE, member.getId(), testMember);
        String createdByMessage = "CreatedBy should be between 2 and 30 characters!";

        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdByMessage))
        );
    }

    @Test
    public void badRequestUpdate() throws Exception {
        Member member = helper.getNewMember("badrequestupdate@MIT.com");
        member.setUserId(UUID.randomUUID());
        member.setUpdatedDate(LocalDateTime.now().minusMinutes(2L).withNano(0));
        member.setCreatedBy("newCreatedBy");
        member.setUpdatedDate(LocalDateTime.now().withNano(0));

        MvcResult mvcResult = super.create(URL_TEMPLATE, member);
        String createdDateMessage = "The createdDate should not be past or future.";
        String userIdMessage = "The userId field must belong to a user.";

        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(createdDateMessage)),
                () -> assertTrue(exceptionMessage.contains(userIdMessage))
        );
    }

    @Test
    public void nonExistentMemberUpdate() throws Exception {
        Member member = helper.getNewMember("nonExistentMember@uit");
        member.setId(UUID.randomUUID());
        MvcResult fourthMvcResult = super.update(URL_TEMPLATE, member.getId(), member);
        String fourthExceptionMessage = Objects.requireNonNull(fourthMvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND.value(), fourthMvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent member!", fourthExceptionMessage)
        );
    }

    @Test
    public void nullUpdatedByFieldUpdate() throws Exception {
        Member member = helper.getNewMember("nullUpdatedByField@uit");
        member.setUpdatedDate(LocalDateTime.now());
        MvcResult fourthMvcResult = super.update(URL_TEMPLATE, member.getId(), member);
        String fourthExceptionMessage = Objects.requireNonNull(fourthMvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), fourthMvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedBy field must be filled. \n", fourthExceptionMessage)
        );
    }

    @Test
    public void nullUpdatedDateFieldUpdate() throws Exception {
        Member member = helper.getNewMember("nullUpdatedDateField@uit");
        member.setUpdatedBy(member.getCreatedBy());
        MvcResult fourthMvcResult = super.update(URL_TEMPLATE, member.getId(), member);
        String fourthExceptionMessage = Objects.requireNonNull(fourthMvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), fourthMvcResult.getResponse().getStatus()),
                () -> assertEquals("The updatedDate field must be filled. \n", fourthExceptionMessage)
        );
    }
}
