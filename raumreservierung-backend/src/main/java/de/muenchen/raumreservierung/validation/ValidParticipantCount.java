package de.muenchen.raumreservierung.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingParticipantValidator.class)
@Documented
public @interface ValidParticipantCount {
    String message() default "Invalid participant count";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
