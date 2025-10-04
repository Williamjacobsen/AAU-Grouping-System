package com.aau.grouping_system.Group;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.aau.grouping_system.User.Student;
import com.aau.grouping_system.User.Supervisor;
import com.aau.grouping_system.database.Database;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final Map<Long, Group> groups = new ConcurrentHashMap<>();
    private final Map<Long, Student> students = new ConcurrentHashMap<>();
    private final Database db;
    
    public GroupController(Database db) {
        this.db = db;
    }
    
    
    @PostMapping("/{groupId}/accept-request/{studentId}")
    public ResponseEntity<String> acceptJoinRequest(
        @PathVariable Long groupId, 
        @PathVariable Long studentId) {
        
        Group group = groups.get(groupId);
        Student student = students.get(studentId);
        
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (student == null) {
            return ResponseEntity.badRequest().body("Student not found");
        }
        
        try {
            group.acceptJoinRequest(student);
            return ResponseEntity.ok("Join request accepted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to accept request: " + e.getMessage());
        }
    }
    
    @GetMapping("/{groupId}/requests")
    public ResponseEntity<Student[]> getJoinRequests(@PathVariable Long groupId) {
        Group group = groups.get(groupId);
        
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(group.getJoinRequests());
    }
    
    @PostMapping("/{groupId}/request-join/{studentId}")
    public ResponseEntity<String> requestToJoin(
        @PathVariable Long groupId,
        @PathVariable Long studentId) {
        
        Group group = groups.get(groupId);
        Student student = students.get(studentId);
        
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (student == null) {
            return ResponseEntity.badRequest().body("Student not found");
        }
        
        try {
            group.requestToJoin(student);
            return ResponseEntity.ok("Join request submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to submit request: " + e.getMessage());
        }
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<Group> getGroup(@PathVariable Long groupId) {
        Group group = groups.get(groupId);
        
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(group);
    }
    
    @GetMapping
    public ResponseEntity<Map<Long, Group>> getAllGroups() {
        return ResponseEntity.ok(groups);
    }
}
