package com.aau.grouping_system.Session;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;

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
		session.setAllowStudentProjectProposals(record.allowStudentProjectProposals());

		LocalDateTime questionnaireDeadline = convertToLocalDateTime(record.questionnaireDeadlineISODateString());
		session.setQuestionnaireDeadline(questionnaireDeadline);

		ApplySupervisorEmails(session, record.supervisorEmails());
		ApplyStudentEmails(session, record.studentEmails());
	}

	private LocalDateTime convertToLocalDateTime(String isoDateString) {
		try {
			Instant instant = Instant.parse(isoDateString);
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
			db.getSupervisors().cascadeRemoveItem(db, (Supervisor) user);
		};

		Consumer<String> createUserFunction = (newEmail) -> {
			db.getSupervisors().addItem(
					session.getSupervisors(),
					new Supervisor(newEmail, "Not specified", session));
		};

		ApplyEmails(session, emailList, getUsersFunction, removeUserFunction, createUserFunction);
	}

	@SuppressWarnings("unchecked") // Type-safety violations aren't true here.
	private void ApplyStudentEmails(Session session, String emailList) {

		Supplier<CopyOnWriteArrayList<User>> getUsersFunction = () -> {
			return (CopyOnWriteArrayList<User>) session.getStudents().getItems(db);
		};

		Consumer<User> removeUserFunction = (user) -> {
			db.getStudents().cascadeRemoveItem(db, (Student) user);
		};

		Consumer<String> createUserFunction = (newEmail) -> {
			db.getStudents().addItem(
					session.getStudents(),
					new Student(newEmail, "Not specified", session));
		};

		ApplyEmails(session, emailList, getUsersFunction, removeUserFunction, createUserFunction);
	}

	private void ApplyEmails(
			Session session,
			String emailList,
			Supplier<CopyOnWriteArrayList<User>> getUsersFunction,
			Consumer<User> removeUserFunction,
			Consumer<String> createUserFunction) {

		// Explanation of Supplier, Consumer, and BiConsumer:
		// "Supplier" is a function with no input and 1 output.
		// "Consumer" is a function with 1 input and no output.

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
				createUserFunction.accept(trimmedEmail);
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
