package spd.trello.validators.annotation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateTimeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Present {
    String message() default "Time should not be past or future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
