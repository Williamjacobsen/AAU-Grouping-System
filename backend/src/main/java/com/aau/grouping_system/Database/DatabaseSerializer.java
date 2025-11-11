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

		Student st1 = studentService.addStudent(se1, "st1", "st1", "Alice");
		Student st2 = studentService.addStudent(se1, "st2", "st2", "Bob");
		Student st3 = studentService.addStudent(se1, "st3", "st3", "Charlie");
		Student st4 = studentService.addStudent(se1, "st4", "st4", "Diana");
		Student st5 = studentService.addStudent(se1, "st5", "st5", "Ethan");
		Student st6 = studentService.addStudent(se1, "st6", "st6", "Fiona");
		Student st7 = studentService.addStudent(se1, "st7", "st7", "George");
		Student st8 = studentService.addStudent(se1, "st8", "st8", "Hannah");
		Student st9 = studentService.addStudent(se1, "st9", "st9", "Ian");
		Student st10 = studentService.addStudent(se1, "st10", "st10", "Julia");
		Student st11 = studentService.addStudent(se1, "st11", "st11", "Kevin");
		Student st12 = studentService.addStudent(se1, "st12", "st12", "Laura");
		Student st13 = studentService.addStudent(se1, "st13", "st13", "Michael");
		Student st14 = studentService.addStudent(se1, "st14", "st14", "Nina");
		Student st15 = studentService.addStudent(se1, "st15", "st15", "Oscar");
		Student st16 = studentService.addStudent(se1, "st16", "st16", "Paula");
		Student st17 = studentService.addStudent(se1, "st17", "st17", "Quinn");
		Student st18 = studentService.addStudent(se1, "st18", "st18", "Riley");
		Student st19 = studentService.addStudent(se1, "st19", "st19", "Sophia");
		Student st20 = studentService.addStudent(se1, "st20", "st20", "Tom");
		Student st21 = studentService.addStudent(se1, "st21", "st21", "Uma");
		Student st22 = studentService.addStudent(se1, "st22", "st22", "Victor");
		Student st23 = studentService.addStudent(se1, "st23", "st23", "Wendy");
		Student st24 = studentService.addStudent(se1, "st24", "st24", "Xander");
		Student st25 = studentService.addStudent(se1, "st25", "st25", "Yara");
		Student st26 = studentService.addStudent(se1, "st26", "st26", "Zane");
		Student st27 = studentService.addStudent(se1, "st27", "st27", "Adam");
		Student st28 = studentService.addStudent(se1, "st28", "st28", "Bella");
		Student st29 = studentService.addStudent(se1, "st29", "st29", "Cody");
		Student st30 = studentService.addStudent(se1, "st30", "st30", "Delia");

		st1.getQuestionnaire().setDesiredProjectId1("AI Chatbot");
		st1.getQuestionnaire().setDesiredProjectId2("Web App");
		st1.getQuestionnaire().setDesiredProjectId3("Data Science");

		st2.getQuestionnaire().setDesiredProjectId1("Web App");
		st2.getQuestionnaire().setDesiredProjectId2("AI Chatbot");
		st2.getQuestionnaire().setDesiredProjectId3("Game Design");

		st3.getQuestionnaire().setDesiredProjectId1("Health Tracker");
		st3.getQuestionnaire().setDesiredProjectId2("AI Chatbot");
		st3.getQuestionnaire().setDesiredProjectId3("IoT System");

		st4.getQuestionnaire().setDesiredProjectId1("Finance Dashboard");
		st4.getQuestionnaire().setDesiredProjectId2("AI Chatbot");
		st4.getQuestionnaire().setDesiredProjectId3("Smart Home");

		st5.getQuestionnaire().setDesiredProjectId1("Smart Home");
		st5.getQuestionnaire().setDesiredProjectId2("Web App");
		st5.getQuestionnaire().setDesiredProjectId3("AI Chatbot");

		st6.getQuestionnaire().setDesiredProjectId1("IoT System");
		st6.getQuestionnaire().setDesiredProjectId2("Finance Dashboard");
		st6.getQuestionnaire().setDesiredProjectId3("Health Tracker");

		st7.getQuestionnaire().setDesiredProjectId1("Finance Dashboard");
		st7.getQuestionnaire().setDesiredProjectId2("AI Chatbot");
		st7.getQuestionnaire().setDesiredProjectId3("Smart Home");

		st8.getQuestionnaire().setDesiredProjectId1("Smart Home");
		st8.getQuestionnaire().setDesiredProjectId2("Web App");
		st8.getQuestionnaire().setDesiredProjectId3("AI Chatbot");

		st9.getQuestionnaire().setDesiredProjectId1("IoT System");
		st9.getQuestionnaire().setDesiredProjectId2("Finance Dashboard");
		st9.getQuestionnaire().setDesiredProjectId3("Health Tracker");

		st10.getQuestionnaire().setDesiredProjectId1("IoT System");
		st10.getQuestionnaire().setDesiredProjectId2("Finance Dashboard");
		st10.getQuestionnaire().setDesiredProjectId3("Smart Home");

		Project p1 = new Project(db, se1.getProjects(), "AI Chatbot", "AI assistant for customer support");
		Project p2 = new Project(db, se1.getProjects(), "Web App", "Interactive web platform");
		Project p3 = new Project(db, se1.getProjects(), "Smart Home", "IoT-based automation system");
		Project p4 = new Project(db, se1.getProjects(), "Health Tracker", "Fitness and health monitoring app");
		Project p5 = new Project(db, se1.getProjects(), "Finance Dashboard", "Financial data visualization tool");
		Project p6 = new Project(db, se1.getProjects(), "Game Design", "Multiplayer online game");
		Project p7 = new Project(db, se1.getProjects(), "E-commerce", "Online shopping platform");
		Project p8 = new Project(db, se1.getProjects(), "Social Media", "New generation social app");
		Project p9 = new Project(db, se1.getProjects(), "Education Portal", "Online learning hub");
		Project p10 = new Project(db, se1.getProjects(), "Weather Forecast", "Climate analysis system");

		Group g1 = new Group(db, se1.getGroups(), su1, p1, "group1@mail.com", 7, 10);
		Group g2 = new Group(db, se1.getGroups(), su1, p2, "group2@mail.com", 7, 10);
		Group g3 = new Group(db, se1.getGroups(), su2, p3, "group3@mail.com", 7, 10);
		Group g4 = new Group(db, se1.getGroups(), su2, p4, "group4@mail.com", 7, 10);
		Group g5 = new Group(db, se1.getGroups(), su2, p5, "group5@mail.com", 7, 10);
		Group g6 = new Group(db, se1.getGroups(), su3, p6, "group6@mail.com", 7, 10);
		Group g7 = new Group(db, se1.getGroups(), su3, p7, "group7@mail.com", 7, 10);
		Group g8 = new Group(db, se1.getGroups(), su3, p8, "group8@mail.com", 7, 10);
		Group g9 = new Group(db, se1.getGroups(), su4, p9, "group9@mail.com", 7, 10);
		Group g10 = new Group(db, se1.getGroups(), su4, p10, "group10@mail.com", 7, 10);

		groupService.joinGroup(g1.getId(), st1);
		groupService.joinGroup(g1.getId(), st2);
		groupService.joinGroup(g1.getId(), st3);

		groupService.joinGroup(g2.getId(), st4);
		groupService.joinGroup(g2.getId(), st5);
		groupService.joinGroup(g2.getId(), st6);
		groupService.joinGroup(g2.getId(), st7);

		groupService.joinGroup(g3.getId(), st8);
		groupService.joinGroup(g3.getId(), st9);
		groupService.joinGroup(g3.getId(), st10);

		groupService.joinGroup(g4.getId(), st11);
		groupService.joinGroup(g4.getId(), st12);
		groupService.joinGroup(g4.getId(), st13);
		groupService.joinGroup(g4.getId(), st14);

		groupService.joinGroup(g5.getId(), st15);
		groupService.joinGroup(g5.getId(), st16);

		groupService.joinGroup(g6.getId(), st17);
		groupService.joinGroup(g6.getId(), st18);
		groupService.joinGroup(g6.getId(), st19);
		groupService.joinGroup(g6.getId(), st20);

		groupService.joinGroup(g7.getId(), st21);
		groupService.joinGroup(g7.getId(), st22);

		groupService.joinGroup(g8.getId(), st23);
		groupService.joinGroup(g8.getId(), st24);
		groupService.joinGroup(g8.getId(), st25);

		groupService.joinGroup(g9.getId(), st26);
		groupService.joinGroup(g9.getId(), st27);

		groupService.joinGroup(g10.getId(), st28);
		groupService.joinGroup(g10.getId(), st29);
		groupService.joinGroup(g10.getId(), st30);

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