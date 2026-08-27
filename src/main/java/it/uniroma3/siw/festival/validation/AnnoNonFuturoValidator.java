package it.uniroma3.siw.festival.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class AnnoNonFuturoValidator implements ConstraintValidator<AnnoNonFuturo, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value <= LocalDate.now().getYear();
    }
}
