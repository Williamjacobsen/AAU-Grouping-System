package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Group.GroupService;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Student.StudentService;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.Supervisor.SupervisorService;

import jakarta.annotation.PostConstruct;

import java.io.*;

@Component
/// A component that handles saving and loading the database.
public class DatabaseSerializer {

	private static final String saveFileName = "databaseDataSaveFile.ser";

	private final Database db;
	private final CoordinatorService coordinatorService;
	private final StudentService studentService;
	private final SupervisorService supervisorService;
	private final GroupService groupService;

	public DatabaseSerializer(Database db, CoordinatorService coordinatorService, StudentService studentService,
			SupervisorService supervisorService, GroupService groupService) {
		this.db = db;
		this.coordinatorService = coordinatorService;
		this.studentService = studentService;
		this.supervisorService = supervisorService;
		this.groupService = groupService;
	}

	public void saveDatabase() {
		// Open file stream
		// (Because we're using try-with-resource statements, we don't need to manually
		// close the stream via "stream.close()" since try-with-resource automatically
		// disposee of its resources.)
		try (FileOutputStream fileOutput = new FileOutputStream(saveFileName)) {
			try (ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput)) {
				// Write database data to file
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
		// disposes of its resources.)
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

		Coordinator c1 = coordinatorService.addCoordinator("c1", "c1", "Coordinator name 1");
		Coordinator c2 = coordinatorService.addCoordinator("c2", "c2", "Coordinator name 2");
		Coordinator c3 = coordinatorService.addCoordinator("c3", "c3", "Coordinator name 3");
		Coordinator c4 = coordinatorService.addCoordinator("c4", "c4", "Coordinator name 4");

		Session se1 = new Session(db, c1.getSessions(), c1, "Session name 1");
		Session se2 = new Session(db, c1.getSessions(), c1, "Session name 2");
		Session se3 = new Session(db, c2.getSessions(), c2, "Session name 3");

		Supervisor su1 = supervisorService.addSupervisor(se1, "su1", "su1", "Supervisor name 1");
		Supervisor su2 = supervisorService.addSupervisor(se1, "su2", "su2", "Supervisor name 2");
		Supervisor su3 = supervisorService.addSupervisor(se1, "su3", "su3", "Supervisor name 3");
		Supervisor su4 = supervisorService.addSupervisor(se2, "su4", "su4", "Supervisor name 4");
		Supervisor su5 = supervisorService.addSupervisor(se2, "su5", "su5", "Supervisor name 5");

		Student st1 = studentService.addStudent(se1, "st1", "st1", "Student name 1");
		Student st2 = studentService.addStudent(se1, "st2", "st2", "Student name 2");
		Student st3 = studentService.addStudent(se1, "st3", "st3", "Student name 3");
		Student st4 = studentService.addStudent(se1, "st4", "st4", "Student name 4");
		Student st5 = studentService.addStudent(se1, "st5", "st5", "Student name 5");
		Student st6 = studentService.addStudent(se1, "st6", "st6", "Student name 6");
		Student st7 = studentService.addStudent(se2, "st7", "st7", "Student name 7");
		Student st8 = studentService.addStudent(se2, "st8", "st8", "Student name 8");
		Student st9 = studentService.addStudent(se2, "st9", "st9", "Student name 9");

		Project p1 = new Project(db, se1.getProjects(), "Project name 1", "Description 1");
		Project p2 = new Project(db, se1.getProjects(), "Project name 2", "Description 2");
		Project p3 = new Project(db, se1.getProjects(), "Project name 3", "Description 3");
		Project p4 = new Project(db, se1.getProjects(), "Project name 4", "Description 4");
		Project p5 = new Project(db, se1.getProjects(), "Project name 5", "Description 5");

		Group g1 = new Group(db, se1.getGroups(), su1, p1, "Group email 1", 10, 10);
		Group g2 = new Group(db, se1.getGroups(), su1, p3, "Group email 2", 10, 10);
		Group g3 = new Group(db, se1.getGroups(), su2, p1, "Group email 3", 10, 10);

		groupService.joinGroup(g1.getId(), st1);
		groupService.joinGroup(g1.getId(), st3);
		groupService.joinGroup(g1.getId(), st5);
		groupService.joinGroup(g2.getId(), st4);

		// For testing purposes, console log student logins so we can log in as them
		System.out.println("---- STUDENT test logins ----");
		System.out.println("Student 1 - Password: st1, ID: " + st1.getId());
		System.out.println("Student 2 - Password: st2, ID: " + st2.getId());
		System.out.println("Student 3 - Password: st3, ID: " + st3.getId());
		System.out.println("Student 4 - Password: st4, ID: " + st4.getId());
		System.out.println("Student 5 - Password: st5, ID: " + st5.getId());
		System.out.println("Student 6 - Password: st6, ID: " + st6.getId());
		System.out.println("Student 7 - Password: st7, ID: " + st7.getId());
		System.out.println("Student 8 - Password: st8, ID: " + st8.getId());
		System.out.println("Student 9 - Password: st9, ID: " + st9.getId());
		System.out.println("---- SUPERVISOR test logins ----");
		System.out.println("Supervisor 1 - Password: su1, ID: " + su1.getId());
		System.out.println("Supervisor 2 - Password: su2, ID: " + su2.getId());
		System.out.println("Supervisor 3 - Password: su3, ID: " + su3.getId());
		System.out.println("Supervisor 4 - Password: su4, ID: " + su4.getId());
		System.out.println("Supervisor 5 - Password: su5, ID: " + su5.getId());
	}

	@PostConstruct
	public void init() throws FileNotFoundException, IOException, ClassNotFoundException {
		// TODO: Make use of the loading and saving methods.
		// System.out.println("Loading database...");
		// loadDatabase();
		// System.out.println("Saving database...");
		// saveDatabase();
		fillDatabaseWithExampleData();
	}

}