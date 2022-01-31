package spd.trello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spd.trello.domain.User;
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
    public void successCreate() {
        User user = new User();
        user.setFirstName("createFirstName");
        user.setLastName("createLastName");
        user.setEmail("create@email");
        User testUser = service.create(user);
        assertNotNull(testUser);
        assertAll(
                () -> assertEquals("createFirstName", testUser.getFirstName()),
                () -> assertEquals("createLastName", testUser.getLastName()),
                () -> assertEquals("create@email", testUser.getEmail()),
                () -> assertEquals(ZoneId.systemDefault().toString(), testUser.getTimeZone())
        );
    }

    @Test
    public void testFindAll() {
        User user = new User();
        user.setFirstName("1FirstName");
        user.setLastName("1LastName");
        user.setEmail("1@email");
        User testFirstUser = service.create(user);
        user.setFirstName("2FirstName");
        user.setLastName("2LastName");
        user.setEmail("2@email");
        User testSecondUser = service.create(user);
        assertNotNull(testFirstUser);
        assertNotNull(testSecondUser);
        List<User> testUsers = service.findAll();
        assertAll(
                () -> assertTrue(testUsers.contains(testFirstUser)),
                () -> assertTrue(testUsers.contains(testSecondUser))
        );
    }

    @Test
    public void createFailure() {
        User user = new User();
        user.setFirstName("createFirstName");
        user.setLastName("createLastName");
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(user),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("User doesn't creates", ex.getMessage());
    }

    @Test
    public void findByIdFailure() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("User with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void testDelete() {
        User user = new User();
        user.setFirstName("deleteFirstName");
        user.setLastName("deleteLastName");
        user.setEmail("delete@email");
        User testUser = service.create(user);
        assertNotNull(testUser);
        UUID id = testUser.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void testUpdate() {
        User user = new User();
        user.setFirstName("updateFirstName");
        user.setLastName("updateLastName");
        user.setEmail("update@email");
        User firstUser = service.create(user);
        assertNotNull(firstUser);
        UUID id = firstUser.getId();
        User testUser = new User();
        testUser.setId(firstUser.getId());
        testUser.setFirstName("newFirstName");
        testUser.setLastName("newLastName");
        testUser.setEmail("new@email");
        testUser.setTimeZone("Europe/Paris");
        service.update(testUser);
        assertAll(
                () -> assertEquals("newFirstName", service.findById(id).getFirstName()),
                () -> assertEquals("newLastName", service.findById(id).getLastName()),
                () -> assertEquals("new@email", service.findById(id).getEmail()),
                () -> assertEquals("Europe/Paris", service.findById(id).getTimeZone())
        );
    }

    @Test
    public void updateFailure() {
        User testUser = new User();
        testUser.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testUser),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("User with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
