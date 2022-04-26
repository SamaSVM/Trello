package spd.trello.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.User;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.services.UserService;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTest {
    @Autowired
    private UserService service;
    @Autowired
    private UnitHelper helper;

    @Test
    public void create() {
        User user = new User();
        user.setFirstName("createFirstName");
        user.setLastName("createLastName");
        user.setEmail("create@email.com");
        user.setTimeZone(ZoneId.systemDefault().toString());
        User testUser = service.save(user);

        assertNotNull(testUser);
        assertAll(
                () -> assertEquals(user.getFirstName(), testUser.getFirstName()),
                () -> assertEquals(user.getLastName(), testUser.getLastName()),
                () -> assertEquals(user.getEmail(), testUser.getEmail()),
                () -> assertEquals(ZoneId.systemDefault().toString(), testUser.getTimeZone())
        );
    }

    @Test
    public void findAll() {
        User firstUser = helper.getNewUser("1findAll@UT.com");
        User secondUser = helper.getNewUser("2findAll@UT.com");

        assertNotNull(firstUser);
        assertNotNull(secondUser);
        List<User> testUsers = service.getAll();
        assertAll(
                () -> assertTrue(testUsers.contains(firstUser)),
                () -> assertTrue(testUsers.contains(secondUser))
        );
    }

    @Test
    public void findById() {
        User user = helper.getNewUser("findById@UT.com");

        User testUser = service.getById(user.getId());
        assertEquals(user, testUser);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@UT.com");

        assertNotNull(user);
        service.delete(user.getId());
        assertFalse(service.getAll().contains(user));
    }

    @Test
    public void update() {
        User user = helper.getNewUser("update@ut.com");
        assertNotNull(user);
        user.setFirstName("newFirstName");
        user.setLastName("newLastName");
        user.setTimeZone("Europe/Paris");
        service.update(user);

        User testUser = service.getById(user.getId());
        assertAll(
                () -> assertEquals(user.getFirstName(), testUser.getFirstName()),
                () -> assertEquals(user.getLastName(), testUser.getLastName()),
                () -> assertEquals(user.getEmail(), testUser.getEmail()),
                () -> assertEquals(user.getTimeZone(), testUser.getTimeZone())
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
        assertEquals("No class spd.trello.domain.User entity with id " + id + " exists!", ex.getMessage());
    }

    @Test
    public void validationCreate() {
        User user = helper.getNewUser("validationcreate@ut.com");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(user), "no exception"
        );
        assertEquals("Email is already in use!", ex.getMessage());
    }

    @Test
    public void emailUpdate() {
        User user = helper.getNewUser("emailUpdate@UT.com");
        user.setEmail("newValidationUpdate@UT.com");
        BadRequestException firstExceptions = assertThrows(
                BadRequestException.class, () -> service.update(user), "no exception"
        );
        assertEquals("The email field cannot be updated!", firstExceptions.getMessage());
    }

    @Test
    public void nonExistentUserUpdate() {
        User user = helper.getNewUser("nonExistentUser@UT.com");
        user.setId(UUID.randomUUID());
        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class, () -> service.update(user), "no exception"
        );
        assertEquals("Cannot update non-existent user!", ex.getMessage());
    }

    @Test
    public void nullFieldFirstNameCreate() {
        User user = new User();
        user.setLastName("createLastName");
        user.setEmail("nullFieldFirstNameCreate@email.com");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(user), "no exception"
        );
        assertEquals("The firstname, lastname and email fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullFieldLastNameCreate() {
        User user = new User();
        user.setFirstName("createFirstName");
        user.setEmail("nullFieldLastNameCreate@email.com");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(user), "no exception"
        );
        assertEquals("The firstname, lastname and email fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullFieldEmailCreate() {
        User user = new User();
        user.setFirstName("createFirstName");
        user.setLastName("createLastName");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(user), "no exception"
        );
        assertEquals("The firstname, lastname and email fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullFieldFirstNameUpdate() {
        User user = helper.getNewUser("nullFieldFirstNameUpdate@UT.com");
        user.setFirstName(null);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(user), "no exception"
        );
        assertEquals("The firstname, lastname and email fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullFieldLastNameUpdate() {
        User user = helper.getNewUser("nullFieldLastNameUpdate@UT.com");
        user.setLastName(null);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(user), "no exception"
        );
        assertEquals("The firstname, lastname and email fields must be filled.", ex.getMessage());
    }

    @Test
    public void nullFieldEmailUpdate() {
        User user = helper.getNewUser("nullFieldEmailUpdate@UT.com");
        user.setEmail(null);
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(user), "no exception"
        );
        assertEquals("The firstname, lastname and email fields must be filled.", ex.getMessage());
    }

    @Test
    public void badFieldEmailCreate() {
        User user = new User();
        user.setFirstName("createFirstName");
        user.setLastName("createLastName");
        user.setEmail("badFieldEmailCreate@gmail");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(user), "no exception"
        );
        assertEquals("The email field should look like email.", ex.getMessage());
    }

    @Test
    public void badFieldTimeZoneCreate() {
        User user = new User();
        user.setFirstName("createFirstName");
        user.setLastName("createLastName");
        user.setEmail("badFieldTimeZoneCreate@gmail.com");
        user.setTimeZone("TimeZone");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(user), "no exception"
        );
        assertEquals("The TimeZone field must be in TimeZone format!", ex.getMessage());
    }

    @Test
    public void badFieldTimeZoneUpdate() {
        User user = helper.getNewUser("badFieldTimeZoneUpdate@UT.com");
        user.setTimeZone("TimeZone");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.update(user), "no exception"
        );
        assertAll(
                () -> assertTrue(ex.getMessage().contains("The TimeZone field must be in TimeZone format!")),
                () -> assertTrue(ex.getMessage().contains("The email field cannot be updated!"))
        );
    }
}
