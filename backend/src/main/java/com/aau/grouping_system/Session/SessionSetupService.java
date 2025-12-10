package com.aau.grouping_system.Session;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Exceptions.RequestException;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.UserService;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;

@Service
public class SessionSetupService {

	private final Database db;
	private final UserService userService;

	public SessionSetupService(
			Database db,
			UserService userService) {
		this.db = db;
		this.userService = userService;
	}

	public void updateSessionSetup(Session session, SessionSetupRecord record) {

		session.setName(record.name());
		session.setMinGroupSize(record.minGroupSize());
		session.setMaxGroupSize(record.maxGroupSize());
		session.setAllowStudentProjectProposals(record.allowStudentProjectProposals());

		LocalDateTime questionnaireDeadline = convertToLocalDateTime(record.questionnaireDeadlineISODateString());
		session.setQuestionnaireDeadline(questionnaireDeadline);

		ApplySupervisorEmailAndNamePairs(session, record.supervisorEmailAndNamePairs());
		ApplyStudentEmailAndNamePairs(session, record.studentEmailAndNamePairs());
	}

	private LocalDateTime convertToLocalDateTime(String isoDateString) {
		try {
			Instant instant = Instant.parse(isoDateString);
			return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format");
		}
	}

	private void ApplySupervisorEmailAndNamePairs(Session session, String emailAndNamePairs) {

		Supplier<CopyOnWriteArrayList<? extends User>> getUsersFunction = () -> {
			return db.getSupervisors().getItems(session.getSupervisors().getIds());
		};

		Consumer<User> removeUserFunction = (user) -> {
			db.getSupervisors().cascadeRemoveItem(db, (Supervisor) user);
		};

		Consumer<EmailAndNamePair> createUserFunction = (emailAndNamePair) -> {
			db.getSupervisors().addItem(
					session.getSupervisors(),
					new Supervisor(emailAndNamePair.email, emailAndNamePair.name, session));
		};

		ApplyEmailAndNamePairs(session, emailAndNamePairs, getUsersFunction, removeUserFunction, createUserFunction);
	}

	private void ApplyStudentEmailAndNamePairs(Session session, String emailAndNamePairs) {

		Supplier<CopyOnWriteArrayList<? extends User>> getUsersFunction = () -> {
			return db.getStudents().getItems(session.getStudents().getIds());
		};

		Consumer<User> removeUserFunction = (user) -> {
			db.getStudents().cascadeRemoveItem(db, (Student) user);
		};

		Consumer<EmailAndNamePair> createUserFunction = (emailAndNamePair) -> {
			db.getStudents().addItem(
					session.getStudents(),
					new Student(emailAndNamePair.email, emailAndNamePair.name, session));
		};

		ApplyEmailAndNamePairs(session, emailAndNamePairs, getUsersFunction, removeUserFunction, createUserFunction);
	}

	private class EmailAndNamePair {
		String email;
		String name;
	}

	private void ApplyEmailAndNamePairs(
			Session session,
			String emailAndNamePairs,
			Supplier<CopyOnWriteArrayList<? extends User>> getUsersFunction,
			Consumer<User> removeUserFunction,
			Consumer<EmailAndNamePair> createUserFunction) {

		// Explanation of Supplier, Consumer, and BiConsumer:
		// "Supplier" is a function with no input and 1 output.
		// "Consumer" is a function with 1 input and no output.

		CopyOnWriteArrayList<? extends User> users = getUsersFunction.get();
		CopyOnWriteArrayList<EmailAndNamePair> pairs = separateEmailAndNamePairs(emailAndNamePairs);

		// Cancel operation, if there are duplicate emails or names
		boolean hasDuplicateEmails = pairs.stream()
				.map(p -> p.email)
				.distinct()
				.count() != pairs.size();
		boolean hasDuplicateNames = pairs.stream()
				.map(p -> p.name)
				.distinct()
				.count() != pairs.size();
		if (hasDuplicateEmails) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Operation canceled, because there are duplicate emails");
		}
		if (hasDuplicateNames) {
			throw new RequestException(HttpStatus.BAD_REQUEST, "Operation canceled, because there are duplicate names");
		}

		// Delete users who are not on the list
		for (User user : users) {
			boolean pairsDoNotContainUser = pairs.stream().noneMatch(pair -> pair.email.equals(user.getEmail()));

			if (pairs.size() == 0 || pairsDoNotContainUser) {
				removeUserFunction.accept(user);
			}
		}

		// Add new users who are on the list, or update the name of an existing one
		for (EmailAndNamePair pair : pairs) {
			User existingUser = null;
			for (User user : users) {
				if (user.getEmail().equals(pair.email)) {
					existingUser = user;
					break;
				}
			}

			if (existingUser == null) {
				createUserFunction.accept(pair);
			} else if (!existingUser.getName().equals(pair.name)) {
				userService.modifyName(pair.name, existingUser);
			}
		}
	}

	private CopyOnWriteArrayList<EmailAndNamePair> separateEmailAndNamePairs(String emailAndNamePairs) {

		// Example of an input string:
		// "an@email.com Alex Alexson \n another@email.com Barry Barryson"

		String[] entries = emailAndNamePairs.trim().split("\\n");
		CopyOnWriteArrayList<String> trimmedEntries = new CopyOnWriteArrayList<>();
		for (String entry : entries) {
			trimmedEntries.add(entry.trim());
		}

		CopyOnWriteArrayList<EmailAndNamePair> pairs = new CopyOnWriteArrayList<>();
		for (String trimmedEntry : trimmedEntries) {
			EmailAndNamePair pair = new EmailAndNamePair();

			int firstSpaceIndex = trimmedEntry.indexOf(' '); // The email is separated from the name via a space
			// String.indexOf(' ') return -1 if the whitespace isn't found
			if (firstSpaceIndex == -1) {
				continue;
			}

			pair.email = trimmedEntry.substring(0, firstSpaceIndex).trim();
			pair.name = trimmedEntry.substring(firstSpaceIndex + 1).trim();
			pairs.add(pair);
		}

		return pairs;
	}
}
