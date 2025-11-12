package com.aau.grouping_system.InputValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = IsListOfEmailsValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsListOfEmails {

	String message() default "must be a list of emails separated by the newline character '\\n'";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
