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
        user.setEmail("create@email");
        user.setTimeZone(ZoneId.systemDefault().toString());
        User testUser = service.save(user);

        assertNotNull(testUser);
        assertAll(
                () -> assertEquals("createFirstName", testUser.getFirstName()),
                () -> assertEquals("createLastName", testUser.getLastName()),
                () -> assertEquals("create@email", testUser.getEmail()),
                () -> assertEquals(ZoneId.systemDefault().toString(), testUser.getTimeZone())
        );
    }

    @Test
    public void findAll() {
        User firstUser = helper.getNewUser("1findAll@UT");
        User secondUser = helper.getNewUser("2findAll@UT");

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
        User user = helper.getNewUser("findById@UT");

        User testUser = service.getById(user.getId());
        assertEquals(user, testUser);
    }

    @Test
    public void delete() {
        User user = helper.getNewUser("delete@UT");

        assertNotNull(user);
        service.delete(user.getId());
        assertFalse(service.getAll().contains(user));
    }

    @Test
    public void update() {
        User firstUser = helper.getNewUser("update@UT");
        assertNotNull(firstUser);
        firstUser.setFirstName("newFirstName");
        firstUser.setLastName("newLastName");
        firstUser.setTimeZone("Europe/Paris");
        service.update(firstUser);

        User testUser = service.getById(firstUser.getId());
        assertAll(
                () -> assertEquals("newFirstName", testUser.getFirstName()),
                () -> assertEquals("newLastName", testUser.getLastName()),
                () -> assertEquals("update@ut", testUser.getEmail()),
                () -> assertEquals("Europe/Paris", testUser.getTimeZone())
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
        User user = helper.getNewUser("validationCreate@UT");
        BadRequestException ex = assertThrows(
                BadRequestException.class, () -> service.save(user), "no exception"
        );
        assertEquals("Email is already in use!", ex.getMessage());
    }

    @Test
    public void validationUpdate() {
        User user = helper.getNewUser("validationUpdate@UT");
        user.setEmail("newValidationUpdate@UT");
        BadRequestException firstExceptions = assertThrows(
                BadRequestException.class, () -> service.update(user), "no exception"
        );
        user.setId(UUID.randomUUID());
        BadRequestException secondExceptions = assertThrows(
                BadRequestException.class, () -> service.update(user), "no exception"
        );
        assertAll(
                () -> assertEquals("The email field cannot be updated!", firstExceptions.getMessage()),
                () -> assertEquals("Cannot update non-existent user!", secondExceptions.getMessage())
        );
    }
}
