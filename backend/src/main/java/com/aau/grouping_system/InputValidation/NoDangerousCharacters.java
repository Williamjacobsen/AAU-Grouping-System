// Tutorial on how to create custom validators: https://www.baeldung.com/javax-validation-method-constraints

package com.aau.grouping_system.InputValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = NoDangerousCharactersValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDangerousCharacters {

	String message() default "must not contain dangerous characters that allow for code injection attacks";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
