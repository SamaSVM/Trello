package spd.trello.validators.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

public class TimeZoneValidator implements ConstraintValidator<TimeZone, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            ZoneId.of(value);
        } catch (ZoneRulesException e) {
            return false;
        }
        return true;
    }
}