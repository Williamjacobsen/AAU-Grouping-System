// Tutorial on how to create custom validators: https://www.baeldung.com/javax-validation-method-constraints

package com.aau.grouping_system.InputValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class NoWhitespaceValidator implements ConstraintValidator<NoWhitespace, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Allow null values (use @NotNull if null should be invalid)
		if (value == null) {
			return true;
		}

		return !value.contains(" ");
	}
}
