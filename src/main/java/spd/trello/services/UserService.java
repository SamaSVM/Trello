package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.User;
import spd.trello.repository.InterfaceRepository;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class UserService extends AbstractService<User> {
    public UserService(InterfaceRepository<User> repository) {
        super(repository);
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
        User oldUser = findById(entity.getId());
        if (entity.getFirstName() == null) {
            entity.setFirstName(oldUser.getFirstName());
        }
        if (entity.getLastName() == null) {
            entity.setLastName(oldUser.getLastName());
        }
        if (entity.getEmail() == null) {
            entity.setEmail(oldUser.getEmail());
        }
        if (entity.getTimeZone() == null) {
            entity.setTimeZone(oldUser.getTimeZone());
        }
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        return repository.delete(id);
    }
}
