package com.aau.grouping_system.Session;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Supervisor.Supervisor;

@Service
public class SessionSetupService {

	private final Database db;

	public SessionSetupService(Database db) {
		this.db = db;
	}

	public void updateSessionSetup(Session session, SessionSetupRecord record) {

		session.setName(record.name());
		session.setMinGroupSize(record.minGroupSize());
		session.setMaxGroupSize(record.maxGroupSize());

		LocalDateTime questionnaireDeadline = convertToLocalDateTime(record.questionnaireDeadlineISOString());
		session.setQuestionnaireDeadline(questionnaireDeadline);

		ApplySupervisorEmails(session, record.supervisorEmails());
		ApplyStudentEmails(session, record.studentEmails());
	}

	private LocalDateTime convertToLocalDateTime(String isoString) {
		try {
			Instant instant = Instant.parse(isoString);
			return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format");
		}
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	private void ApplySupervisorEmails(Session session, String emailList) {

		Supplier<CopyOnWriteArrayList<User>> getUsersFunction = () -> {
			return (CopyOnWriteArrayList<User>) session.getSupervisors().getItems(db);
		};

		Consumer<User> removeUserFunction = (user) -> {
			db.getSupervisors().cascadeRemove(db, (Supervisor) user);
		};

		BiConsumer<String, String> createUserFunction = (newEmail, newPassword) -> {
			new Supervisor(db, session.getSupervisors(), newEmail, newPassword, "Not specified", session);
		};

		ApplyEmails(session, emailList, getUsersFunction, removeUserFunction, createUserFunction);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	private void ApplyStudentEmails(Session session, String emailList) {

		Supplier<CopyOnWriteArrayList<User>> getUsersFunction = () -> {
			return (CopyOnWriteArrayList<User>) session.getStudents().getItems(db);
		};

		Consumer<User> removeUserFunction = (user) -> {
			db.getStudents().cascadeRemove(db, (Student) user);
		};

		BiConsumer<String, String> createUserFunction = (newEmail, newPassword) -> {
			new Student(db, session.getStudents(), newEmail, newPassword, "Not specified", session);
		};

		ApplyEmails(session, emailList, getUsersFunction, removeUserFunction, createUserFunction);
	}

	private void ApplyEmails(
			Session session,
			String emailList,
			Supplier<CopyOnWriteArrayList<User>> getUsersFunction,
			Consumer<User> removeUserFunction,
			BiConsumer<String, String> createUserFunction) {

		// Explanation of Supplier, Consumer, and BiConsumer:
		// "Supplier" is a function with no input and 1 output.
		// "Consumer" is a function with 1 input and no output.
		// "BiConsumer" is a function with 2 input and no output.

		CopyOnWriteArrayList<User> users = getUsersFunction.get();
		CopyOnWriteArrayList<String> trimmedEmails = trimEmails(emailList);

		// Remove old entries not on the list
		for (User user : users) {
			if (!trimmedEmails.contains(user.getEmail())) {
				removeUserFunction.accept(user);
			}
		}

		// Add new entries from list
		for (String trimmedEmail : trimmedEmails) {
			Boolean doesExist = users.stream()
					.anyMatch(user -> user.getEmail().equals(trimmedEmail));

			if (!doesExist) {
				String randomPasswordHash = UUID.randomUUID().toString();
				createUserFunction.accept(trimmedEmail, randomPasswordHash);
			}
		}
	}

	private CopyOnWriteArrayList<String> trimEmails(String emailsString) {
		String[] emails = emailsString.split("\\n");
		CopyOnWriteArrayList<String> trimmedEmails = new CopyOnWriteArrayList<>();
		for (String email : emails) {
			trimmedEmails.add(email.trim());
		}
		return trimmedEmails;
	}
}
