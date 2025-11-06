// Tutorial on how to create custom validators: https://www.baeldung.com/javax-validation-method-constraints

package com.aau.grouping_system.InputValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class NoDangerousCharactersValidator implements ConstraintValidator<NoDangerousCharacters, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Allow null values (use @NotNull if null should be invalid)
		if (value == null) {
			return true;
		}

		String[] dangerousCharacters = {
				// XSS attack chracters
				"&",
				"\"",
				"\'",
				"<",
				">",
				"/",
				"\\",
				// Path traversal attack characters
				"..",
		};

		for (String s : dangerousCharacters) {
			if (value.contains(s)) {
				return false;
			}
		}

		return true;
	}
}
