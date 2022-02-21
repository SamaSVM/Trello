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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MemberIntegrationTest extends AbstractIntegrationTest<User>{
    private final String URL_TEMPLATE = "/members";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        User user = helper.getNewUser("create@MIT");
        Member firstMember = new Member();
        firstMember.setUserId(user.getId());
        firstMember.setCreatedBy(user.getEmail());
        firstMember.setMemberRole(MemberRole.ADMIN);
        MvcResult firstMvcResult = super.create(URL_TEMPLATE, firstMember);

        Member secondMember = new Member();
        secondMember.setUserId(user.getId());
        secondMember.setCreatedBy(user.getEmail());
        MvcResult secondMvcResult = super.create(URL_TEMPLATE, secondMember);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(firstMvcResult, "$.id")),
                () -> assertEquals(firstMember.getCreatedBy(), getValue(firstMvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(firstMvcResult, "$.createdDate")),
                () -> assertNull(getValue(firstMvcResult, "$.updatedBy")),
                () -> assertNull(getValue(firstMvcResult, "$.updatedDate")),
                () -> assertEquals(user.getId().toString(), getValue(firstMvcResult, "$.userId")),
                () -> assertEquals(firstMember.getMemberRole().toString(), getValue(firstMvcResult, "$.memberRole")),

                () -> assertEquals(HttpStatus.CREATED.value(), secondMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(secondMvcResult, "$.id")),
                () -> assertEquals(secondMember.getCreatedBy(), getValue(secondMvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(secondMvcResult, "$.createdDate")),
                () -> assertNull(getValue(secondMvcResult, "$.updatedBy")),
                () -> assertNull(getValue(secondMvcResult, "$.updatedDate")),
                () -> assertEquals(user.getId().toString(), getValue(secondMvcResult, "$.userId")),
                () -> assertEquals(MemberRole.GUEST.toString(), getValue(secondMvcResult, "$.memberRole"))
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
        Member firstMember = helper.getNewMember("1findAll@MIT");
        Member secondMember = helper.getNewMember("2findAll@MIT");
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
        Member member = helper.getNewMember("findById@MIT");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, member.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(member.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
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
        Member member = helper.getNewMember("deleteById@MIT");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, member.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<Member> testUsers = helper.getMembersArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testUsers.contains(member))
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
        Member member = helper.getNewMember("update@MIT");
        member.setUpdatedBy(member.getCreatedBy());
        member.setMemberRole(MemberRole.ADMIN);
        MvcResult mvcResult = super.update(URL_TEMPLATE, member.getId(), member);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(member.getCreatedBy(), getValue(mvcResult, "$.createdBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.createdDate")),
                () -> assertEquals(member.getCreatedBy(), getValue(mvcResult, "$.updatedBy")),
                () -> assertEquals(String.valueOf(LocalDate.now()), getValue(mvcResult, "$.updatedDate")),
                () -> assertEquals(member.getUserId().toString(), getValue(mvcResult, "$.userId")),
                () -> assertEquals(member.getMemberRole().toString(), getValue(mvcResult, "$.memberRole"))
        );
    }
}
