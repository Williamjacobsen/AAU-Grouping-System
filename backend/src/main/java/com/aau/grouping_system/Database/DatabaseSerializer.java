package com.aau.grouping_system.Database;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;

import jakarta.annotation.PostConstruct;

import java.io.*;

@Component
@DependsOn("database")
public class DatabaseSerializer {

	private static final String saveFileName = "saveFiles/databaseDataSaveFile.ser";

	private final Database db;

	public DatabaseSerializer(Database db) {
		this.db = db;
	}

	public void saveDatabase() throws FileNotFoundException, IOException {

		// Open stream
		FileOutputStream fileOutput = new FileOutputStream(saveFileName);
		ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);

		// Write object to file
		objectOutput.writeObject(db.getData());

		// Close stream
		objectOutput.close();
		fileOutput.close();
	}

	public void loadDatabase() {
		try {
			// Open stream
			FileInputStream fileInput = new FileInputStream(saveFileName);
			ObjectInputStream objectInput = new ObjectInputStream(fileInput);

			// Read and load saved database
			DatabaseData savedData = (DatabaseData) objectInput.readObject();
			db.setData(savedData);

			// Close stream
			objectInput.close();
			fileInput.close();
		} catch (Exception exception) {
			if (exception instanceof FileNotFoundException) {
				System.out.println("No save file found. Reverting to default data."); // TODO: Remove this
				fillDatabaseWithExampleData();
			} else {
				throw new RuntimeException("Error while loading database save file.");
			}
		}
	}

	@SuppressWarnings("unused") // To suppress warnings relative to unused code.
	private void fillDatabaseWithExampleData() {

		Coordinator c0 = new Coordinator(db.getData().getCoordinators(), null, db,
				"Coordinator0@example.com", "PasswordHash0", "Coordinator0");
		Coordinator c1 = new Coordinator(db.getData().getCoordinators(), null,
				db, "Coordinator1@example.com", "PasswordHash1", "Coordinator1");
		Coordinator c2 = new Coordinator(db.getData().getCoordinators(), null,
				db, "Coordinator2@example.com", "PasswordHash2", "Coordinator2");
		Coordinator c3 = new Coordinator(db.getData().getCoordinators(), null,
				db, "Coordinator3@example.com", "PasswordHash3", "Coordinator3");

		Session s0 = new Session(db.getData().getSessions(), c0.sessions, db, c0);
		Session s1 = new Session(db.getData().getSessions(), c0.sessions, db, c0);
		Session s2 = new Session(db.getData().getSessions(), c1.sessions, db, c1);

		Student st0 = new Student(db.getData().getStudents(), s0.students, "Student0@example.com", "PasswordHash0",
				"Student0", s0);
		Student st1 = new Student(db.getData().getStudents(), s0.students, "Student1@example.com", "PasswordHash1",
				"Student1", s0);
		Student st2 = new Student(db.getData().getStudents(), s0.students, "Student2@example.com", "PasswordHash2",
				"Student2", s0);
		Student st3 = new Student(db.getData().getStudents(), s1.students, "Student3@example.com", "PasswordHash3",
				"Student3", s1);
		Student st4 = new Student(db.getData().getStudents(), s1.students, "Student4@example.com", "PasswordHash4",
				"Student4", s1);
		Student st5 = new Student(db.getData().getStudents(), s2.students, "Student5@example.com", "PasswordHash5",
				"Student5", s2);
		Student st6 = new Student(db.getData().getStudents(), s2.students, "Student6@example.com", "PasswordHash6",
				"Student6", s2);
	}

	@PostConstruct
	public void init() throws FileNotFoundException, IOException, ClassNotFoundException {
		// TODO: Remove
		System.out.println("----------------");
		System.out.println("----------------");
		System.out.println("----------------");
		System.out.println("----------------");
		System.out.println("amount of coordinators = " + db.getData().getCoordinators().getAllItems().size());
		for (Coordinator coordinator : db.getData().getCoordinators().getAllItems().values()) {
			System.out.println("coordinator.getEmail = " + coordinator.getEmail());
		}
		System.out.println("--- Loading");
		loadDatabase();
		System.out.println("amount of coordinators = " + db.getData().getCoordinators().getAllItems().size());
		for (Coordinator coordinator : db.getData().getCoordinators().getAllItems().values()) {
			System.out.println("coordinator.getEmail = " + coordinator.getEmail());
		}
		System.out.println("--- Saving");
		saveDatabase();
		System.out.println("--- Loading");
		loadDatabase();
		System.out.println("amount of coordinators = " + db.getData().getCoordinators().getAllItems().values().size());
		for (Coordinator coordinator : db.getData().getCoordinators().getAllItems().values()) {
			System.out.println("coordinator.getEmail = " + coordinator.getEmail());
		}
		for (Session session : db.getData().getSessions().getAllItems().values()) {
			System.out.println("session.getId = " + session.getId());
		}
	}

}