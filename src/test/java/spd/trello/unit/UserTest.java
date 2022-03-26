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
        firstUser.setEmail("new@email");
        firstUser.setTimeZone("Europe/Paris");
        service.update(firstUser);

        User testUser = service.getById(firstUser.getId());
        assertAll(
                () -> assertEquals("newFirstName", testUser.getFirstName()),
                () -> assertEquals("newLastName", testUser.getLastName()),
                () -> assertEquals("update@UT", testUser.getEmail()),
                () -> assertEquals("Europe/Paris", testUser.getTimeZone())
        );
    }

    @Test
    public void createFailure() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.save(new User()),
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
        assertEquals("No class spd.trello.domain.User entity with id " + id + " exists!", ex.getMessage());
    }
}
