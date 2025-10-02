import java.util.List;

public class SessionFactory {
    public static Session create(Coordinator coordinator, List<Supervisor> supervisors, List<Student> students,
                                 List<Project> projects, List<Group> groups) {
        return new Session(coordinator, supervisors, students, projects, groups);
    }
}