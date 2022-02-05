package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.User;
import spd.trello.repository.InterfaceRepository;

import java.time.ZoneId;
import java.util.UUID;

@Service
public class UserService extends AbstractService<User> {
    public UserService(InterfaceRepository<User> repository) {
        super(repository);
    }

    @Override
    public User create(User entity) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        user.setTimeZone(entity.getTimeZone() == null ? ZoneId.systemDefault().toString() : entity.getTimeZone());
        repository.create(user);
        return repository.findById(user.getId());
    }

    @Override
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
}
