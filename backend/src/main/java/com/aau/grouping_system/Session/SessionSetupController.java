package com.aau.grouping_system.Session;

import com.aau.grouping_system.EmailSystem.EmailService;
import com.aau.grouping_system.User.Student.Student;
import com.aau.grouping_system.User.Student.StudentService;
import com.aau.grouping_system.User.Supervisor.Supervisor;
import com.aau.grouping_system.User.Supervisor.SupervisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sessions/{sessionId}")
public class SessionSetupController {

    @Autowired private StudentService studentService;
    @Autowired private SupervisorService supervisorService;
    @Autowired private EmailService emailService;

    @PostMapping("/sendLoginCodeToStudents")
    public ResponseEntity<?> sendLoginCodeToStudents(
            @PathVariable String sessionId,
            @RequestBody SendLoginCodeRequest request) {
        try {
            List<Student> students = studentService.getStudentsBySessionId(sessionId);

            if (request.isSendOnlyNew()) {
                students = students.stream()
                        .filter(s -> s.getLoginCode() == null)
                        .collect(Collectors.toList());
            }

            int sent = 0;
            for (Student s : students) {
                String code = s.getLoginCode();
                if (code == null) {
                    code = generateLoginCode();
                    s.setLoginCode(code);
                    studentService.saveStudent(s);
                }

                String subject = "AAU Grouping System - Your Login Code";
                String body = """
                        Hello,

                        Your login code for the AAU Grouping System is: %s

                        Please use this code to access your student page and submit your project preferences.

                        Best regards,
                        AAU Grouping System
                        """.formatted(code);

                emailService.builder()
                        .to(s.getEmail())
                        .subject(subject)
                        .text(body)
                        .send();
                sent++;
            }
            return ResponseEntity.ok("Emails sent: " + sent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/sendLoginCodeToSupervisors")
    public ResponseEntity<?> sendLoginCodeToSupervisors(
            @PathVariable String sessionId,
            @RequestBody SendLoginCodeRequest request) {
        try {
            List<Supervisor> supervisors = supervisorService.getSupervisorsBySessionId(sessionId);

            if (request.isSendOnlyNew()) {
                supervisors = supervisors.stream()
                        .filter(sv -> sv.getLoginCode() == null)
                        .collect(Collectors.toList());
            }

            int sent = 0;
            for (Supervisor sv : supervisors) {
                String code = sv.getLoginCode();
                if (code == null) {
                    code = generateLoginCode();
                    sv.setLoginCode(code);
                    supervisorService.saveSupervisor(sv);
                }

                String subject = "AAU Grouping System - Your Login Code";
                String body = """
                        Hello,

                        Your login code for the AAU Grouping System is: %s

                        Please use this code to access your supervisor page and manage your projects.

                        Best regards,
                        AAU Grouping System
                        """.formatted(code);

                emailService.builder()
                        .to(sv.getEmail())
                        .subject(subject)
                        .text(body)
                        .send();
                sent++;
            }
            return ResponseEntity.ok("Emails sent: " + sent);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    private String generateLoginCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
		static class SendLoginCodeRequest {
			private boolean sendOnlyNew;
	
			public boolean isSendOnlyNew() {
					return sendOnlyNew;
			}
			public void setSendOnlyNew(boolean sendOnlyNew) {
					this.sendOnlyNew = sendOnlyNew;
			}
	}
}