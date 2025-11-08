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

    @Autowired
    private StudentService studentService;

    @Autowired
    private SupervisorService supervisorService;

    @PostMapping("/sendLoginCodeToStudents")
    public ResponseEntity<?> sendLoginCodeToStudents(
            @PathVariable String sessionId,
            @RequestBody SendLoginCodeRequest request) {
        try {
            List<Student> students = studentService.getStudentsBySessionId(sessionId);
            
            // Filter if sendOnlyNew is true
            if (request.isSendOnlyNew()) {
                students = students.stream()
                    .filter(student -> student.getLoginCode() == null)
                    .collect(Collectors.toList());
            }

            // Generate and save login codes for each student
            for (Student student : students) {
                String loginCode = generateLoginCode();
                student.setLoginCode(loginCode);
                studentService.saveStudent(student);

                // Send email with login code
                String subject = "AAU Grouping System - Your Login Code";
                String body = String.format("""
                    Hello,
                    
                    Your login code for the AAU Grouping System is: %s
                    
                    Please use this code to access your student page and submit your project preferences.
                    
                    Best regards,
                    AAU Grouping System""", loginCode);

                EmailService.sendEmail(student.getEmail(), subject, body);
            }

            return ResponseEntity.ok().build();
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
            
            // Filter if sendOnlyNew is true
            if (request.isSendOnlyNew()) {
                supervisors = supervisors.stream()
                    .filter(supervisor -> supervisor.getLoginCode() == null)
                    .collect(Collectors.toList());
            }

            // Generate and save login codes for each supervisor
            for (Supervisor supervisor : supervisors) {
                String loginCode = generateLoginCode();
                supervisor.setLoginCode(loginCode);
                supervisorService.saveSupervisor(supervisor);

                // Send email with login code
                String subject = "AAU Grouping System - Your Login Code";
                String body = String.format("""
                    Hello,
                    
                    Your login code for the AAU Grouping System is: %s
                    
                    Please use this code to access your supervisor page and manage your projects.
                    
                    Best regards,
                    AAU Grouping System""", loginCode);

                EmailService.sendEmail(supervisor.getEmail(), subject, body);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private String generateLoginCode() {
        // Generate a random 6-character code
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}

class SendLoginCodeRequest {
    private boolean sendOnlyNew;

    public boolean isSendOnlyNew() {
        return sendOnlyNew;
    }

    public void setSendOnlyNew(boolean sendOnlyNew) {
        this.sendOnlyNew = sendOnlyNew;
    }
}