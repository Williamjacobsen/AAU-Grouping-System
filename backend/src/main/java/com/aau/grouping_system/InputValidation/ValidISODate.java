package com.aau.grouping_system.InputValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ValidISODateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidISODate {

	String message() default "must be a valid ISO 8601 date (formatted like this: '2007-12-03T10:15:30.00Z')";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}