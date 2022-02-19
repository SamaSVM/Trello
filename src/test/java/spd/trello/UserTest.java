package spd.trello;

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
        User firstUser = new User();
        firstUser.setFirstName("1FirstName");
        firstUser.setLastName("1LastName");
        firstUser.setEmail("1@email");
        firstUser.setTimeZone("Europe/Paris");
        User testFirstUser = service.save(firstUser);

        User secondUser = new User();
        secondUser.setFirstName("2FirstName");
        secondUser.setLastName("2LastName");
        secondUser.setEmail("2@email");
        User testSecondUser = service.save(secondUser);

        assertNotNull(testFirstUser);
        assertNotNull(testSecondUser);
        List<User> testUsers = service.getAll();
        assertAll(
                () -> assertTrue(testUsers.contains(testFirstUser)),
                () -> assertTrue(testUsers.contains(testSecondUser))
        );
    }

    @Test
    public void findById() {
        User user = new User();
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setEmail("findById@UT");
        user.setTimeZone("Europe/Kiev");
        service.save(user);

        User testUser = service.getById(user.getId());
        assertEquals(user, testUser);
    }

    @Test
    public void delete() {
        User user = new User();
        user.setFirstName("deleteFirstName");
        user.setLastName("deleteLastName");
        user.setEmail("delete@email");
        User testUser = service.save(user);

        assertNotNull(testUser);
        service.delete(testUser.getId());
        assertFalse(service.getAll().contains(testUser));
    }

    @Test
    public void update() {
        User user = new User();
        user.setFirstName("updateFirstName");
        user.setLastName("updateLastName");
        user.setEmail("update@email");
        User firstUser = service.save(user);

        assertNotNull(firstUser);
        UUID id = firstUser.getId();
        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setFirstName("newFirstName");
        updateUser.setLastName("newLastName");
        updateUser.setEmail("new@email");
        updateUser.setTimeZone("Europe/Paris");
        service.update(updateUser);

        User testUser = service.getById(id);
        assertAll(
                () -> assertEquals("newFirstName", testUser.getFirstName()),
                () -> assertEquals("newLastName", testUser.getLastName()),
                () -> assertEquals("update@email", testUser.getEmail()),
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
