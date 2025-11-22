package com.aau.grouping_system.Database;

import org.springframework.stereotype.Component;

import com.aau.grouping_system.Group.Group;
import com.aau.grouping_system.Group.GroupService;
import com.aau.grouping_system.Project.Project;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.Coordinator.CoordinatorService;
import com.aau.grouping_system.User.SessionMember.SessionMember;
import com.aau.grouping_system.User.SessionMember.SessionMemberService;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Student.StudentService;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.User.SessionMember.Supervisor.SupervisorService;
import com.aau.grouping_system.User.UserService;
import jakarta.annotation.PostConstruct;

import java.io.*;
import java.util.ArrayList;

@Component
/// A component that handles saving and loading the database.
public class DatabaseSerializer {

	private static final String saveFileName = "databaseDataSaveFile.ser";

	private final Database db;
	private final CoordinatorService coordinatorService;
	private final StudentService studentService;
	private final SupervisorService supervisorService;
	private final GroupService groupService;
	private final UserService userService;

	public DatabaseSerializer(
			Database db,
			CoordinatorService coordinatorService,
			StudentService studentService,
			SupervisorService supervisorService,
			GroupService groupService,
			SessionMemberService sessionMemberService,
			UserService userService) {
		this.db = db;
		this.coordinatorService = coordinatorService;
		this.studentService = studentService;
		this.supervisorService = supervisorService;
		this.groupService = groupService;
		this.userService = userService;
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
			// TODO: This exception should be updated to be handled with more care before
			// product deployment. But it's fine here in the prototyping stage.
			System.out.println("No database data save file found. Reverting to default data.");
			fillDatabaseWithExampleData();
		} catch (IOException exception) {
			throw new RuntimeException("Error loading database data from save file: I/O exception.");
		}
	}

	private void fillDatabaseWithExampleData() {

		ArrayList<Coordinator> co = new ArrayList<>();
		co.add(coordinatorService.addCoordinator("c1", "c1", "Coordinator name 1"));
		co.add(coordinatorService.addCoordinator("c2", "c2", "Coordinator name 2"));
		co.add(coordinatorService.addCoordinator("c3", "c3", "Coordinator name 3"));
		co.add(coordinatorService.addCoordinator("c4", "c4", "Coordinator name 4"));

		ArrayList<Session> se = new ArrayList<>();
		se.add(db.getSessions().addItem(db, co.get(0).getSessions(), new Session(db, co.get(0), "Session name 1")));
		se.get(0).setMinGroupSize(6);
		se.get(0).setMinGroupSize(7);
		se.add(db.getSessions().addItem(db, co.get(0).getSessions(), new Session(db, co.get(0), "Session name 2")));
		se.get(1).setMinGroupSize(3);
		se.get(1).setMinGroupSize(5);
		se.add(db.getSessions().addItem(db, co.get(1).getSessions(), new Session(db, co.get(1), "Session name 3")));
		se.get(2).setMinGroupSize(6);
		se.get(2).setMinGroupSize(7);

		ArrayList<Supervisor> su = new ArrayList<>();
		su.add(supervisorService.addSupervisor(se.get(0), "su1@example.com", "su1", "Supervisor name 1"));
		su.add(supervisorService.addSupervisor(se.get(0), "su2@example.com", "su2", "Supervisor name 2"));
		su.add(supervisorService.addSupervisor(se.get(0), "su3@example.com", "su3", "Supervisor name 3"));
		su.add(supervisorService.addSupervisor(se.get(1), "su4@example.com", "su4", "Supervisor name 4"));
		su.add(supervisorService.addSupervisor(se.get(1), "su5@example.com", "su5", "Supervisor name 5"));

		ArrayList<Student> st = new ArrayList<>();
		st.add(studentService.addStudent(se.get(0), "st1@example.com", "Alice"));
		st.add(studentService.addStudent(se.get(0), "st2@example.com", "Bob"));
		st.add(studentService.addStudent(se.get(0), "st3@example.com", "Charlie"));
		st.add(studentService.addStudent(se.get(0), "st4@example.com", "Diana"));
		st.add(studentService.addStudent(se.get(0), "st5@example.com", "Ethan"));
		st.add(studentService.addStudent(se.get(0), "st6@example.com", "Fiona"));
		st.add(studentService.addStudent(se.get(0), "st7@example.com", "George"));
		st.add(studentService.addStudent(se.get(0), "st8@example.com", "Hannah"));
		st.add(studentService.addStudent(se.get(0), "st9@example.com", "Ian"));
		st.add(studentService.addStudent(se.get(0), "st10@example.com", "Julia"));
		st.add(studentService.addStudent(se.get(0), "st11@example.com", "Kevin"));
		st.add(studentService.addStudent(se.get(0), "st12@example.com", "Laura"));
		st.add(studentService.addStudent(se.get(0), "st13@example.com", "Michael"));
		st.add(studentService.addStudent(se.get(0), "st14@example.com", "Nina"));
		st.add(studentService.addStudent(se.get(0), "st15@example.com", "Oscar"));
		st.add(studentService.addStudent(se.get(0), "st16@example.com", "Paula"));
		st.add(studentService.addStudent(se.get(0), "st17@example.com", "Quinn"));
		st.add(studentService.addStudent(se.get(0), "st18@example.com", "Riley"));
		st.add(studentService.addStudent(se.get(0), "st19@example.com", "Sophia"));
		st.add(studentService.addStudent(se.get(0), "st20@example.com", "Tom"));
		st.add(studentService.addStudent(se.get(0), "st21@example.com", "Uma"));
		st.add(studentService.addStudent(se.get(0), "st22@example.com", "Victor"));
		st.add(studentService.addStudent(se.get(0), "st23@example.com", "Wendy"));
		st.add(studentService.addStudent(se.get(0), "st24@example.com", "Xander"));
		st.add(studentService.addStudent(se.get(0), "st25@example.com", "Yara"));
		st.add(studentService.addStudent(se.get(0), "st26@example.com", "Zane"));
		st.add(studentService.addStudent(se.get(0), "st27@example.com", "Adam"));
		st.add(studentService.addStudent(se.get(0), "st28@example.com", "Bella"));
		st.add(studentService.addStudent(se.get(0), "st29@example.com", "Cody"));
		st.add(studentService.addStudent(se.get(0), "st30@example.com", "Delia"));

		ArrayList<Project> pr = new ArrayList<>();
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"AI Chatbot",
				"AI assistant for customer support. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Web App",
				"Interactive web platform. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Smart Home",
				"IoT-based automation system. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Health Tracker",
				"Fitness and health monitoring app. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				su.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Finance Dashboard",
				"Financial data visualization tool. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				su.get(1))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Game Design",
				"Multiplayer online game. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"E-commerce",
				"Online shopping platform. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Social Media",
				"New generation social app. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Education Portal",
				"Online learning hub. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));
		pr.add(db.getProjects().addItem(db, se.get(0).getProjects(), new Project(
				"Weather Forecast",
				"Climate analysis system. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
				co.get(0))));

		st.get(0).getQuestionnaire().setDesiredProjectId1(pr.get(3).getId());
		st.get(0).getQuestionnaire().setDesiredProjectId2(pr.get(2).getId());

		st.get(1).getQuestionnaire().setDesiredProjectId1(pr.get(5).getId());

		st.get(2).getQuestionnaire().setDesiredProjectId1(pr.get(4).getId());
		st.get(2).getQuestionnaire().setDesiredProjectId2(pr.get(3).getId());

		st.get(3).getQuestionnaire().setDesiredProjectId1(pr.get(3).getId());
		st.get(3).getQuestionnaire().setDesiredProjectId2(pr.get(2).getId());
		st.get(3).getQuestionnaire().setDesiredProjectId3(pr.get(6).getId());

		st.get(4).getQuestionnaire().setDesiredProjectId1(pr.get(4).getId());
		st.get(4).getQuestionnaire().setDesiredProjectId2(pr.get(2).getId());
		st.get(4).getQuestionnaire().setDesiredProjectId3(pr.get(1).getId());

		ArrayList<Group> gr = new ArrayList<>();
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 1")));
		gr.get(0).setDesiredProjectId1(pr.get(2).getId());
		gr.get(0).setDesiredProjectId2(pr.get(3).getId());
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 2")));
		gr.get(1).setDesiredProjectId1(pr.get(4).getId());
		gr.get(1).setDesiredProjectId2(pr.get(3).getId());
		gr.get(1).setDesiredProjectId3(pr.get(6).getId());
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 3")));
		gr.get(2).setDesiredProjectId1(pr.get(6).getId());
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 4")));
		gr.get(3).setDesiredProjectId1(pr.get(5).getId());
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 5")));
		gr.get(4).setDesiredProjectId1(pr.get(1).getId());
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 6")));
		gr.get(5).setDesiredProjectId1(pr.get(6).getId());
		gr.get(5).setDesiredProjectId2(pr.get(1).getId());
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 7")));
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 8")));
		gr.get(7).setDesiredProjectId1(pr.get(2).getId());
		gr.get(7).setDesiredProjectId2(pr.get(0).getId());
		gr.get(7).setDesiredProjectId3(pr.get(7).getId());
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 9")));
		gr.add(db.getGroups().addItem(db, se.get(0).getGroups(), new Group(se.get(0), "Group name 10")));
		gr.get(9).setDesiredProjectId1(pr.get(4).getId());

		groupService.joinGroup(gr.get(0), st.get(0));
		groupService.joinGroup(gr.get(0), st.get(1));
		groupService.joinGroup(gr.get(0), st.get(2));

		groupService.joinGroup(gr.get(1), st.get(3));
		groupService.joinGroup(gr.get(1), st.get(4));
		groupService.joinGroup(gr.get(1), st.get(5));
		groupService.joinGroup(gr.get(1), st.get(6));

		groupService.joinGroup(gr.get(2), st.get(7));
		groupService.joinGroup(gr.get(2), st.get(8));
		groupService.joinGroup(gr.get(2), st.get(9));

		groupService.joinGroup(gr.get(3), st.get(10));
		groupService.joinGroup(gr.get(3), st.get(11));
		groupService.joinGroup(gr.get(3), st.get(12));
		groupService.joinGroup(gr.get(3), st.get(13));

		groupService.joinGroup(gr.get(4), st.get(14));
		groupService.joinGroup(gr.get(4), st.get(15));

		groupService.joinGroup(gr.get(5), st.get(16));
		groupService.joinGroup(gr.get(5), st.get(17));
		groupService.joinGroup(gr.get(5), st.get(18));
		groupService.joinGroup(gr.get(5), st.get(19));

		groupService.joinGroup(gr.get(6), st.get(20));
		groupService.joinGroup(gr.get(6), st.get(21));

		groupService.joinGroup(gr.get(7), st.get(22));
		groupService.joinGroup(gr.get(7), st.get(23));
		groupService.joinGroup(gr.get(7), st.get(24));

		groupService.joinGroup(gr.get(8), st.get(25));
		groupService.joinGroup(gr.get(8), st.get(26));

		// For testing purposes, console log logins so we can log in as them
		System.out.println("\n\n\n---- STUDENTS - Generating passwords ----");
		for (int i = 0; i < st.size(); i++) {
			Student student = st.get(i);
			String newPassword = "st" + (i + 1);
			userService.modifyPassword(newPassword, student);
			System.out.println("---" +
					"\nName: " + student.getName() +
					"\nID: " + student.getId() +
					"\nNew password: " + newPassword);
		}
		System.out.println("---- SUPERVISORS - Generating passwords ----");
		for (int i = 0; i < su.size(); i++) {
			Supervisor supervisor = su.get(i);
			String newPassword = "su" + (i + 1);
			userService.modifyPassword(newPassword, supervisor);
			System.out.println("---" +
					"\nName: " + supervisor.getName() +
					"\nID: " + supervisor.getId() +
					"\nNew password: " + newPassword);
		}
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