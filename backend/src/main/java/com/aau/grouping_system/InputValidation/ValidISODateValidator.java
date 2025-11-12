// Tutorial on how to create custom validators: https://www.baeldung.com/javax-validation-method-constraints

package com.aau.grouping_system.InputValidation;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class ValidISODateValidator implements ConstraintValidator<ValidISODate, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		// Allow null values (use @NotNull if null should be invalid)
		if (value == null) {
			return true;
		}

		if (value.trim().isEmpty()) {
			return false;
		}

		try {
			// "Instant.parse" requires a valid ISO string, else it throws an exception
			Instant.parse(value);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
}
