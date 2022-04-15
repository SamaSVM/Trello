package spd.trello.validators.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class DateTimeValidator implements ConstraintValidator<Present, LocalDateTime> {
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value == null || (LocalDateTime.now().minusMinutes(1L).isBefore(value) &&
                LocalDateTime.now().plusMinutes(1L).isAfter(value));
    }
}