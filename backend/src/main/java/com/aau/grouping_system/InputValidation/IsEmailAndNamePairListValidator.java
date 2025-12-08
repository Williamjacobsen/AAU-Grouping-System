// Tutorial on how to create custom validators: https://www.baeldung.com/javax-validation-method-constraints

package com.aau.grouping_system.InputValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class IsEmailAndNamePairListValidator implements ConstraintValidator<IsEmailAndNamePairList, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// Allow null values (use @NotNull if null should be invalid)
		if (value == null) {
			return true;
		}

		// If the string is empty, it is considered valid
		if (value.trim().isEmpty()) {
			return true;
		}

		// Example of an input string:
		// "an@email.com Alex Alexson \n another@email.com Barry Barryson"

		String[] entries = value.split("\\n");
		for (String entry : entries) {
			String trimmedEntry = entry.trim();

			// Skip empty lines
			if (trimmedEntry.isEmpty()) {
				continue;
			}

			// No dangerous characters?
			if (!NoDangerousCharactersValidator.doesNotContainDangerousCharacter(trimmedEntry)) {
				return false;
			}

			// Find and validate email
			int firstSpaceIndex = trimmedEntry.indexOf(' '); // The email is separated from the name via a space
			String trimmedEmail = trimmedEntry.substring(0, firstSpaceIndex).trim();
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
