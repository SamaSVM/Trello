package spd.trello.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import spd.trello.domain.User;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserIntegrationTest extends AbstractIntegrationTest<User>{
    private final String URL_TEMPLATE = "/users";

    @Autowired
    private IntegrationHelper helper;

    @Test
    public void create() throws Exception {
        User firstUser = new User();
        firstUser.setEmail("1create@UIT");
        firstUser.setFirstName("first name");
        firstUser.setLastName("last name");
        MvcResult firstMvcResult = super.create(URL_TEMPLATE, firstUser);

        User secondUser = new User();
        secondUser.setEmail("2create@UIT");
        secondUser.setFirstName("first name");
        secondUser.setLastName("last name");
        secondUser.setTimeZone("Europe/Paris");
        MvcResult secondMvcResult = super.create(URL_TEMPLATE, secondUser);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(firstMvcResult, "$.id")),
                () -> assertEquals(firstUser.getEmail(), getValue(firstMvcResult, "$.email")),
                () -> assertEquals(firstUser.getFirstName(), getValue(firstMvcResult, "$.firstName")),
                () -> assertEquals(firstUser.getLastName(), getValue(firstMvcResult, "$.lastName")),
                () -> assertEquals(ZoneId.systemDefault().toString(), getValue(firstMvcResult, "$.timeZone")),

                () -> assertEquals(HttpStatus.CREATED.value(), secondMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(secondMvcResult, "$.id")),
                () -> assertEquals(secondUser.getEmail(), getValue(secondMvcResult, "$.email")),
                () -> assertEquals(secondUser.getFirstName(), getValue(secondMvcResult, "$.firstName")),
                () -> assertEquals(secondUser.getLastName(), getValue(secondMvcResult, "$.lastName")),
                () -> assertEquals(secondUser.getTimeZone(), getValue(secondMvcResult, "$.timeZone"))
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
        User firsUser = helper.getNewUser("1findAll@UIT");
        User secondUser = helper.getNewUser("2findAll@UIT");
        MvcResult mvcResult = super.findAll(URL_TEMPLATE);
        List<User> testUsers = helper.getUsersArray(mvcResult);

        assertAll(
                () -> assertEquals(MediaType.APPLICATION_JSON.toString(), mvcResult.getResponse().getContentType()),
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(testUsers.contains(firsUser)),
                () -> assertTrue(testUsers.contains(secondUser))
        );
    }

    @Test
    public void findById() throws Exception {
        User user = helper.getNewUser("findById@UIT");
        MvcResult mvcResult = super.findById(URL_TEMPLATE, user.getId());

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(user.getEmail(), getValue(mvcResult, "$.email")),
                () -> assertEquals(user.getFirstName(), getValue(mvcResult, "$.firstName")),
                () -> assertEquals(user.getLastName(), getValue(mvcResult, "$.lastName")),
                () -> assertEquals(ZoneId.systemDefault().toString(), getValue(mvcResult, "$.timeZone"))
        );
    }

    @Test
    public void findByIdFailure() throws Exception {
        MvcResult mvcResult = super.findById(URL_TEMPLATE, UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    public void deleteById() throws Exception {
        User user = helper.getNewUser("deleteById@UIT");
        MvcResult mvcResult = super.deleteById(URL_TEMPLATE, user.getId());
        MvcResult deleteMvcResult = super.findAll(URL_TEMPLATE);
        List<User> testUsers = helper.getUsersArray(deleteMvcResult);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertFalse(testUsers.contains(user))
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
        User user = helper.getNewUser("update@UIT");
        user.setFirstName("new first name");
        user.setLastName("new last name");
        user.setTimeZone("Europe/Paris");
        MvcResult mvcResult = super.update(URL_TEMPLATE, user.getId(), user);

        assertAll(
                () -> assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(mvcResult, "$.id")),
                () -> assertEquals(user.getEmail(), getValue(mvcResult, "$.email")),
                () -> assertEquals(user.getFirstName(), getValue(mvcResult, "$.firstName")),
                () -> assertEquals(user.getLastName(), getValue(mvcResult, "$.lastName")),
                () -> assertEquals(user.getTimeZone(), getValue(mvcResult, "$.timeZone"))
        );
    }
}
