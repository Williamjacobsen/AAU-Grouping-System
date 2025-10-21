package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.User.Student.Student;

import jakarta.annotation.PostConstruct;

import java.io.*;

@Component
public class DatabaseSerializer {

	private static final String saveFileName = "databaseDataSaveFile.ser";

	private final Database db;
	private final CoordinatorService coordinatorService;

	public DatabaseSerializer(Database db, CoordinatorService coordinatorService) {
		this.db = db;
		this.coordinatorService = coordinatorService;
	}

	public void saveDatabase() {
		// Open file stream
		// (Because we're using try-with-resource statements, we don't need to manually
		// close the stream via "stream.close()" since try-with-resource automatically
		// disposees of its resources.)
		try (FileOutputStream fileOutput = new FileOutputStream(saveFileName)) {
			try (ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {
				// Write datbase data to file
				objectOutput.writeObject(db.getData());
			}
		} catch (IOException exception) {
			throw new RuntimeException("Error saving database data to save file: I/O exception.");
		}
	}

	public void loadDatabase() {
		// Open file stream
		// (Because we're using try-with-resource statements, we don't need to manually
		// close the stream via "stream.close()" since try-with-resource automatically
		// disposees of its resources.)
		try (FileInputStream fileInput = new FileInputStream(saveFileName)) {
			try (ObjectInputStream objectInput = new ObjectInputStream(fileInput)) {
				// Read and load saved database data
				DatabaseData savedData = (DatabaseData) objectInput.readObject();
				db.setData(savedData);
			}
		} catch (ClassNotFoundException exception) {
			throw new RuntimeException(
					"Error loading database data from save file: Save file is a not of the (newest) type of DatabaseData.");
		} catch (FileNotFoundException exception) {
			// TODO: This should be updated before product deployment. But it's fine here in
			// the prototyping stage.
			System.out.println("No database data save file found. Reverting to default data.");
			fillDatabaseWithExampleData();
		} catch (IOException exception) {
			throw new RuntimeException("Error loading database data from save file: I/O exception.");
		}
	}

	@SuppressWarnings("unused") // To suppress warnings about unused code.
	private void fillDatabaseWithExampleData() {

		Coordinator c1 = coordinatorService.addCoordinator("c1", "c1", "Coordinator 1");
		Coordinator c2 = coordinatorService.addCoordinator("c2", "c2", "Coordinator 2");
		Coordinator c3 = coordinatorService.addCoordinator("c3", "c3", "Coordinator 3");
		Coordinator c4 = coordinatorService.addCoordinator("c4", "c4", "Coordinator 4");

		Session s1 = new Session(db, c1.sessions, c1);
		Session s2 = new Session(db, c1.sessions, c1);
		Session s3 = new Session(db, c2.sessions, c2);

		Student st1 = new Student(db, s1.students, "s1", "s2", "Student 1", s1);
		Student st2 = new Student(db, s1.students, "s2", "s2", "Student 2", s1);
		Student st3 = new Student(db, s1.students, "s3", "s3", "Student 3", s1);
		Student st4 = new Student(db, s2.students, "s4", "s4", "Student 4", s2);
		Student st5 = new Student(db, s2.students, "s5", "s5", "Student 5", s2);
		Student st6 = new Student(db, s3.students, "s6", "s6", "Student 6", s3);
		Student st7 = new Student(db, s3.students, "s7", "s7", "Student 7", s3);
	}

	@PostConstruct
	public void init() throws FileNotFoundException, IOException, ClassNotFoundException {
		// System.out.println("Loading database...");
		// loadDatabase();
		// System.out.println("Saving database...");
		// saveDatabase();
		fillDatabaseWithExampleData();

		// TODO: remove this:
		for (Session session : db.getData().getSessions().getAllItems().values()) {
			System.out.println("session.getId = " + session.getId());
		}
	}

}