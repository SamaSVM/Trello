package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.User;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.UserRepository;

import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator extends AbstractValidator<User> {

    private final UserRepository repository;

    public UserValidator(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validateSaveEntity(User entity) {
        StringBuilder exceptions = new StringBuilder();
        checkNullFields(entity);
        if (repository.existsByEmail(entity.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }
        Pattern email = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+[.]+[a-zA-Z0-9.-]+$");
        Matcher matcher = email.matcher(entity.getEmail());

        if (!matcher.find()) {
            exceptions.append("The email field should look like email.");
        }
        checkUserFields(exceptions, entity);
    }

    @Override
    public void validateUpdateEntity(User entity) {
        StringBuilder exceptions = new StringBuilder();
        var oldUser = repository.findById(entity.getId());
        if (oldUser.isEmpty()) {
            throw new ResourceNotFoundException("Cannot update non-existent user!");
        }
        if (!oldUser.get().getEmail().equals(entity.getEmail())) {
            exceptions.append("The email field cannot be updated!");
        }
        checkUserFields(exceptions, entity);
    }

    private void checkUserFields(StringBuilder exceptions, User entity) {
        checkNullFields(entity);
        if (entity.getFirstName().length() < 2 || entity.getFirstName().length() > 20) {
            exceptions.append("The firstname field must be between 2 and 20 characters long.");
        }
        if (entity.getLastName().length() < 2 || entity.getLastName().length() > 20) {
            exceptions.append("The lastname field must be between 2 and 20 characters long.");
        }

        try {
            ZoneId.of(entity.getTimeZone());
        } catch (ZoneRulesException e) {
            exceptions.append("The TimeZone field must be in TimeZone format!");
        }
        throwException(exceptions);
    }

    private void checkNullFields(User entity) {
        if (entity.getFirstName() == null || entity.getLastName() == null || entity.getEmail() == null) {
            throw new BadRequestException("The firstname, lastname and email fields must be filled.");
        }
    }

    public void throwException(StringBuilder exceptions) {
        if (exceptions.length() != 0) {
            throw new BadRequestException(exceptions.toString());
        }
    }
}
