package it.uniroma3.siw.festival.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AnnoNonFuturoValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnoNonFuturo {

    String message() default "L'anno non può essere nel futuro";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
