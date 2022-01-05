package spd.trello.services;

import spd.trello.domain.User;
import spd.trello.repository.InterfaceRepository;

import java.time.ZoneId;
import java.util.UUID;

public class UserService extends AbstractService<User> {

    public UserService(InterfaceRepository<User> repository) {
        super(repository);
    }

    public User findById(UUID id) {
        return repository.findById(id);
    }

    public User create(String firstName, String lastName, String email) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setTimeZone(ZoneId.systemDefault().toString());
        repository.create(user);
        return repository.findById(user.getId());
    }

    public User update(User entity) {
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        return repository.delete(id);
    }
}
