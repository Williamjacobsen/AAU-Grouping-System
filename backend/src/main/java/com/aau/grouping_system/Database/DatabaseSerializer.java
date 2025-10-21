package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Student.Student;

import jakarta.annotation.PostConstruct;

import java.io.*;

@Component
public class DatabaseSerializer {

	private static final String saveFileName = "saveFiles/databaseDataSaveFile.ser";

	private final Database db;

	public DatabaseSerializer(Database db) {
		this.db = db;
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

		Coordinator c0 = new Coordinator(db, null, "c0", "c0", "Coordinator0");
		Coordinator c1 = new Coordinator(db, null, "c1", "c1", "Coordinator1");
		Coordinator c2 = new Coordinator(db, null, "c2", "c2", "Coordinator2");
		Coordinator c3 = new Coordinator(db, null, "c3", "c3", "Coordinator3");

		Session s0 = new Session(db, c0.sessions, c0);
		Session s1 = new Session(db, c0.sessions, c0);
		Session s2 = new Session(db, c1.sessions, c1);

		Student st0 = new Student(db, s0.students, "s0", "s0", "Student0", s0);
		Student st1 = new Student(db, s0.students, "s1", "s2", "Student1", s0);
		Student st2 = new Student(db, s0.students, "s2", "s2", "Student2", s0);
		Student st3 = new Student(db, s1.students, "s3", "s3", "Student3", s1);
		Student st4 = new Student(db, s1.students, "s4", "s4", "Student4", s1);
		Student st5 = new Student(db, s2.students, "s5", "s5", "Student5", s2);
		Student st6 = new Student(db, s2.students, "s6", "s6", "Student6", s2);
	}

	@PostConstruct
	public void init() throws FileNotFoundException, IOException, ClassNotFoundException {
		System.out.println("Loading database...");
		loadDatabase();
		System.out.println("Saving database...");
		saveDatabase();
		// TODO: remove this:
		for (Session session : db.getData().getSessions().getAllItems().values()) {
			System.out.println("session.getId = " + session.getId());
		}
	}

}