package spd.trello;

import spd.trello.domain.User;
import spd.trello.repository.UserRepository;
import spd.trello.services.UserService;

import java.util.UUID;

import static spd.trello.BaseTest.dataSource;

public class Helper {
    private static final UserService userService = new UserService(new UserRepository(dataSource));

    public static User getNewUser() {
        return userService.create("testFirstName", "testLastName", "test@mail");
    }

    public static boolean deleteUser(UUID uuid) {
        return userService.delete(uuid);
    }
}
