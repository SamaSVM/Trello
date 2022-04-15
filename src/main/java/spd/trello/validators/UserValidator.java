package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.User;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.UserRepository;

@Component
public class UserValidator extends AbstractValidator<User> {

    private final UserRepository repository;

    public UserValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validateSaveEntity(User entity) {
        if (repository.existsByEmail(entity.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        super.validateSaveEntity(entity);
    }

    @Override
    public void validateUpdateEntity(User entity) {
        User oldUser = repository.getById(entity.getId());
        if (!oldUser.getEmail().equals(entity.getEmail())) {
            throw new BadRequestException("The email field cannot be updated!");
        }
        super.validateUpdateEntity(entity);
    }
}
