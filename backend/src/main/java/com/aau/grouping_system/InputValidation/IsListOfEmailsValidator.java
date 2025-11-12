// Tutorial on how to create custom validators: https://www.baeldung.com/javax-validation-method-constraints

package com.aau.grouping_system.InputValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class IsListOfEmailsValidator implements ConstraintValidator<IsListOfEmails, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Allow null values (use @NotNull if null should be invalid)
		if (value == null) {
			return true;
		}

		// If the string is empty, it's considered valid (no emails)
		if (value.trim().isEmpty()) {
			return true;
		}

		// Split by newline and validate each email
		String[] emails = value.split("\\n");

		for (String email : emails) {
			String trimmedEmail = email.trim();

			// Skip empty lines
			if (trimmedEmail.isEmpty()) {
				continue;
			}

			if (!isValidEmail(trimmedEmail)) {
				return false;
			}
		}

		return true;
	}

	private boolean isValidEmail(String email) {

		// Ensure an "@" character is in the email
		int atIndex = email.indexOf('@');
		boolean atIsFirstCharacter = (atIndex <= 0);
		boolean atIsLastChracter = (atIndex == email.length() - 1);
		if (atIsFirstCharacter || atIsLastChracter) {
			return false;
		}

		// Ensure a "." character is in the email after the "@"
		String domain = email.substring(atIndex + 1);
		int dotIndex = domain.indexOf('.');
		boolean dotIsFirstCharacterAfterAt = (dotIndex <= 0);
		boolean dotIsLastCharacterAfterAt = (dotIndex == domain.length() - 1);
		if (dotIsFirstCharacterAfterAt || dotIsLastCharacterAfterAt) {
			return false;
		}

		return true;
	}
}
