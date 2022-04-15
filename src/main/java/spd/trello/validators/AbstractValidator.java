package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.perent.Domain;
import spd.trello.exeption.BadRequestException;

@Component
public abstract class AbstractValidator<T extends Domain> implements CommonValidator<T> {
    @Override
    public void validateSaveEntity(T entity) {
        if (entity.getId() == null) {
            throw new BadRequestException("Must be field: id");
        }
    }

    @Override
    public void validateUpdateEntity(T entity) {
        if (entity.getId() == null) {
            throw new BadRequestException("Must be field: id");
        }
    }
}
