package spd.trello.validators.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimeZoneValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeZone {
    String message() default "The TimeZone field must be in TimeZone format!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
