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
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserIntegrationTest extends AbstractIntegrationTest<User> {
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
                () -> assertEquals(firstUser.getEmail().toLowerCase(Locale.ROOT),
                        getValue(firstMvcResult, "$.email")),
                () -> assertEquals(firstUser.getFirstName(), getValue(firstMvcResult, "$.firstName")),
                () -> assertEquals(firstUser.getLastName(), getValue(firstMvcResult, "$.lastName")),
                () -> assertEquals(ZoneId.systemDefault().toString(), getValue(firstMvcResult, "$.timeZone")),

                () -> assertEquals(HttpStatus.CREATED.value(), secondMvcResult.getResponse().getStatus()),
                () -> assertNotNull(getValue(secondMvcResult, "$.id")),
                () -> assertEquals(secondUser.getEmail().toLowerCase(Locale.ROOT),
                        getValue(secondMvcResult, "$.email")),
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
        User user = helper.getNewUser("update@uit");
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

    @Test
    public void nullFieldsCreate() throws Exception {
        User user = new User();
        MvcResult mvcResult = super.create(URL_TEMPLATE, user);
        String firstNameMessage = "The firstname field must be filled.";
        String lastNameMessage = "The lastname field must be filled.";
        String emailMessage = "The email field must be filled.";
        String ExceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(ExceptionMessage.contains(firstNameMessage)),
                () -> assertTrue(ExceptionMessage.contains(lastNameMessage)),
                () -> assertTrue(ExceptionMessage.contains(emailMessage))
        );
    }

    @Test
    public void badFieldsCreate() throws Exception {
        User user = new User();
        user.setFirstName("f");
        user.setLastName("l");
        user.setEmail("email");
        user.setTimeZone("zone");

        MvcResult mvcResult = super.create(URL_TEMPLATE, user);
        String firstNameMessage = "The firstname field must be between 2 and 20 characters long.";
        String lastNameMessage = "The lastname field must be between 2 and 20 characters long.";
        String emailMessage = "The email field should look like email.";
        String timeZoneMessage = "The TimeZone field must be in TimeZone format!";
        String exceptionMessage = Objects.requireNonNull(mvcResult.getResolvedException()).getMessage();

        assertAll(

                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus()),
                () -> assertTrue(exceptionMessage.contains(firstNameMessage)),
                () -> assertTrue(exceptionMessage.contains(lastNameMessage)),
                () -> assertTrue(exceptionMessage.contains(emailMessage)),
                () -> assertTrue(exceptionMessage.contains(timeZoneMessage))
        );
    }

    @Test
    public void repeatedEmailCreate() throws Exception {
        User user = helper.getNewUser("repeatedemailcreate@uit");
        user.setId(UUID.randomUUID());
        MvcResult thirdMvcResult = super.create(URL_TEMPLATE, user);
        String thirdExceptionMessage = Objects.requireNonNull(thirdMvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), thirdMvcResult.getResponse().getStatus()),
                () -> assertEquals("Email is already in use!", thirdExceptionMessage)
        );
    }

    @Test
    public void nullFieldsUpdate() throws Exception {
        User user = new User();
        MvcResult firstMvcResult = super.update(URL_TEMPLATE, user.getId(), user);
        String firstFirstNameMessage = "The firstname field must be filled.";
        String firstLastNameMessage = "The lastname field must be filled.";
        String firstEmailMessage = "The email field must be filled.";
        String firstExceptionMessage = Objects.requireNonNull(firstMvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), firstMvcResult.getResponse().getStatus()),
                () -> assertTrue(firstExceptionMessage.contains(firstFirstNameMessage)),
                () -> assertTrue(firstExceptionMessage.contains(firstLastNameMessage)),
                () -> assertTrue(firstExceptionMessage.contains(firstEmailMessage))
        );
    }

    @Test
    public void badFieldsUpdate() throws Exception {
        User user = new User();
        user.setFirstName("f");
        user.setLastName("l");
        user.setEmail("email");
        user.setTimeZone("zone");
        MvcResult secondMvcResult = super.update(URL_TEMPLATE, user.getId(), user);
        String secondFirstNameMessage = "The firstname field must be between 2 and 20 characters long.";
        String secondLastNameMessage = "The lastname field must be between 2 and 20 characters long.";
        String secondEmailMessage = "The email field should look like email.";
        String secondTimeZoneMessage = "The TimeZone field must be in TimeZone format!";
        String secondExceptionMessage = Objects.requireNonNull(secondMvcResult.getResolvedException()).getMessage();


        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), secondMvcResult.getResponse().getStatus()),
                () -> assertTrue(secondExceptionMessage.contains(secondFirstNameMessage)),
                () -> assertTrue(secondExceptionMessage.contains(secondLastNameMessage)),
                () -> assertTrue(secondExceptionMessage.contains(secondEmailMessage)),
                () -> assertTrue(secondExceptionMessage.contains(secondTimeZoneMessage))
        );
    }

    @Test
    public void emailUpdate() throws Exception {
        User testUser = helper.getNewUser("emailupdate@uit");
        testUser.setEmail("new@uit");
        MvcResult thirdMvcResult = super.update(URL_TEMPLATE, testUser.getId(), testUser);
        String thirdExceptionMessage = Objects.requireNonNull(thirdMvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), thirdMvcResult.getResponse().getStatus()),
                () -> assertEquals("The email field cannot be updated!", thirdExceptionMessage)
        );
    }

    @Test
    public void nonExistentUserUpdate() throws Exception {
        User testUser = helper.getNewUser("nonexistentuser@uit");
        MvcResult fourthMvcResult = super.update(URL_TEMPLATE, UUID.randomUUID(), testUser);
        String fourthExceptionMessage = Objects.requireNonNull(fourthMvcResult.getResolvedException()).getMessage();

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), fourthMvcResult.getResponse().getStatus()),
                () -> assertEquals("Cannot update non-existent user!", fourthExceptionMessage)
        );
    }
}
