package com.aau.grouping_system.Group;

import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aau.grouping_system.Config.CorsConfig;
import com.aau.grouping_system.Config.SecurityConfig;
import com.aau.grouping_system.Database.Database;
import com.aau.grouping_system.Database.DatabaseItemChildGroup;
import com.aau.grouping_system.Database.DatabaseMap;
import com.aau.grouping_system.Session.Session;
import com.aau.grouping_system.User.Coordinator.Coordinator;
import com.aau.grouping_system.User.SessionMember.Student.Student;
import com.aau.grouping_system.User.SessionMember.Supervisor.Supervisor;
import com.aau.grouping_system.User.User;
import com.aau.grouping_system.Utils.RequestRequirementService;

import jakarta.servlet.http.HttpServletRequest;

@WebMvcTest(GroupController.class)
@AutoConfigureWebMvc
@ComponentScan(basePackages = { "com.aau.grouping_system.Group", "com.aau.grouping_system.Exceptions" })
@Import({ GroupControllerIntegrationTest.TestConfig.class, SecurityConfig.class, CorsConfig.class })
class GroupControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private Database database;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private RequestRequirementService requestRequirementService;

    private Group mockGroup;
    private Session mockSession;
    private Coordinator mockCoordinator;
    private Student mockStudent;
    private Student mockSecondStudent;
    private Supervisor mockSupervisor;
    private User mockUser;
    private DatabaseItemChildGroup mockGroupsGroup;
    private DatabaseMap<Group> mockGroupMap;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        // Create mock objects
        mockGroup = mock(Group.class);
        mockSession = mock(Session.class);
        mockCoordinator = mock(Coordinator.class);
        mockStudent = mock(Student.class);
        mockSecondStudent = mock(Student.class);
        mockSupervisor = mock(Supervisor.class);
        mockUser = mock(User.class);
        mockGroupsGroup = mock(DatabaseItemChildGroup.class);
        mockGroupMap = mock(DatabaseMap.class);
        
        // Setup database mock
        when(database.getGroups()).thenReturn(mockGroupMap);
        
        // Setup session mock
        when(mockSession.getId()).thenReturn("session-123");
        when(mockSession.getName()).thenReturn("Test Session");
        when(mockSession.getGroups()).thenReturn(mockGroupsGroup);
        when(mockSession.getMaxGroupSize()).thenReturn(5);
        
        // Setup group mock
        when(mockGroup.getId()).thenReturn("group-123");
        when(mockGroup.getName()).thenReturn("Test Group");
        when(mockGroup.getStudentIds()).thenReturn(new CopyOnWriteArrayList<>());
        
        // Setup user mocks
        when(mockCoordinator.getId()).thenReturn("coordinator-123");
        when(mockCoordinator.getRole()).thenReturn(User.Role.Coordinator);
        when(mockStudent.getId()).thenReturn("student-123");
        when(mockStudent.getRole()).thenReturn(User.Role.Student);
        when(mockSecondStudent.getId()).thenReturn("student-456");
        when(mockSecondStudent.getRole()).thenReturn(User.Role.Student);
        when(mockSupervisor.getId()).thenReturn("supervisor-123");
        when(mockSupervisor.getRole()).thenReturn(User.Role.Supervisor);
        when(mockUser.getId()).thenReturn("user-123");
    }

    @Test
    void testAcceptJoinRequest_ValidRequest_ReturnsSuccess() throws Exception {
        // Arrange
        String sessionId = "session-123";
        String groupId = "group-123";
        String studentId = "student-123";

        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);
        when(requestRequirementService.requireGroupExists(groupId))
                .thenReturn(mockGroup);
        when(requestRequirementService.requireStudentExists(studentId))
                .thenReturn(mockStudent);
        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        doNothing().when(groupService).acceptJoinRequest(mockGroup, mockStudent);

        // Act & Assert
        mockMvc.perform(post("/groups/{sessionId}/{groupId}/accept-request/{studentId}", 
                sessionId, groupId, studentId)
                .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(content().string("Join request accepted successfully"));

        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireGroupExists(groupId);
        verify(requestRequirementService).requireStudentExists(studentId);
        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
        verify(requestRequirementService).requireQuestionnaireDeadlineNotExceeded(mockSession);
        verify(groupService).requireUserOwnsGroupOrIsCoordinator(mockGroup, mockUser);
        verify(groupService).acceptJoinRequest(mockGroup, mockStudent);
    }

    @Test
    void testRequestToJoin_ValidStudent_ReturnsSuccess() throws Exception {
        // Arrange
        String sessionId = "session-123";
        String groupId = "group-123";

        when(requestRequirementService.requireUserStudentExists(any(HttpServletRequest.class)))
                .thenReturn(mockStudent);
        when(requestRequirementService.requireGroupExists(groupId))
                .thenReturn(mockGroup);
        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        doNothing().when(groupService).requestToJoin(mockGroup, mockStudent);

        // Act & Assert
        mockMvc.perform(post("/groups/{sessionId}/{groupId}/request-to-join", 
                sessionId, groupId)
                .sessionAttr("user", mockStudent))
                .andExpect(status().isOk())
                .andExpect(content().string("Join request submitted successfully"));

        verify(requestRequirementService).requireUserStudentExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireGroupExists(groupId);
        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockStudent);
        verify(requestRequirementService).requireQuestionnaireDeadlineNotExceeded(mockSession);
        verify(groupService).requestToJoin(mockGroup, mockStudent);
    }

    @Test
    void testGetGroup_ValidGroupId_ReturnsGroup() throws Exception {
        // Arrange
        String groupId = "group-123";

        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);
        when(requestRequirementService.requireGroupExists(groupId))
                .thenReturn(mockGroup);

        // Act & Assert
        mockMvc.perform(get("/groups/{groupId}", groupId)
                .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("group-123"))
                .andExpect(jsonPath("$.name").value("Test Group"));

        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService, times(2)).requireGroupExists(groupId);
    }

    @Test
    void testGetGroups_ValidSession_ReturnsGroupList() throws Exception {
        // Arrange
        String sessionId = "session-123";
        CopyOnWriteArrayList<Group> groups = new CopyOnWriteArrayList<>();
        groups.add(mockGroup);

        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);
        doReturn(groups).when(mockGroupsGroup).getItems(database);

        // Act & Assert
        mockMvc.perform(get("/groups/{sessionId}/getGroups", sessionId)
                .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("group-123"));

        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
    }

    @Test
    void testLeaveGroup_ValidStudentLeavingSelf_ReturnsSuccess() throws Exception {
        // Arrange
        String sessionId = "session-123";
        String groupId = "group-123";
        String studentId = "student-123";

        when(mockStudent.getRole()).thenReturn(User.Role.Student);
        when(mockStudent.getId()).thenReturn(studentId);
        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockStudent);
        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        when(requestRequirementService.requireGroupExists(groupId))
                .thenReturn(mockGroup);
        when(requestRequirementService.requireStudentExists(studentId))
                .thenReturn(mockStudent);
        doNothing().when(groupService).leaveGroup(mockGroup, mockStudent);

        // Act & Assert
        mockMvc.perform(post("/groups/{sessionId}/{groupId}/leave/{studentId}", 
                sessionId, groupId, studentId)
                .sessionAttr("user", mockStudent))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully left the group"));

        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService, times(2)).requireStudentExists(studentId);
        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireQuestionnaireDeadlineNotExceeded(mockSession);
        verify(requestRequirementService).requireGroupExists(groupId);
        verify(groupService).leaveGroup(mockGroup, mockStudent);
    }

    @Test
    void testMoveStudentBetweenGroups_ValidCoordinator_ReturnsSuccess() throws Exception {
        // Arrange
        String fromGroupId = "group-123";
        String toGroupId = "group-456";
        String studentId = "student-123";
        String sessionId = "session-123";
        
        Group fromGroup = mock(Group.class);
        Group toGroup = mock(Group.class);

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        when(requestRequirementService.requireStudentExists(studentId))
                .thenReturn(mockStudent);
        when(mockGroupMap.getItem(fromGroupId))
                .thenReturn(fromGroup);
        when(requestRequirementService.requireGroupExists(toGroupId))
                .thenReturn(toGroup);
        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        doNothing().when(groupService).leaveGroup(fromGroup, mockStudent);
        doNothing().when(groupService).joinGroup(toGroup, mockStudent);

        // Act & Assert
        mockMvc.perform(post("/groups/{fromGroupId}/move-student/{toGroupId}/{studentId}/{sessionId}", 
                fromGroupId, toGroupId, studentId, sessionId)
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isOk())
                .andExpect(content().string("Student moved successfully."));

        verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireCoordinatorIsAuthorizedSession(sessionId, mockCoordinator);
        verify(requestRequirementService).requireStudentExists(studentId);
        verify(requestRequirementService).requireGroupExists(toGroupId);
        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(groupService).leaveGroup(fromGroup, mockStudent);
        verify(groupService).joinGroup(toGroup, mockStudent);
    }

    @Test
    void testCreateGroup_ValidRequest_ReturnsSuccess() throws Exception {
        // Arrange
        String sessionId = "session-123";
        String requestBody = """
                {
                    "name": "New Test Group",
                    "studentId": "student-123"
                }
                """;

        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);
        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        when(requestRequirementService.requireStudentExists("student-123"))
                .thenReturn(mockStudent);
        doNothing().when(groupService).createGroup(mockSession, "New Test Group", mockStudent);

        // Act & Assert
        mockMvc.perform(post("/groups/{sessionId}/createGroup", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .sessionAttr("user", mockUser))
                .andExpect(status().isCreated())
                .andExpect(content().string("Group succesfully created"));

        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireStudentExists("student-123");
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
        verify(groupService).requireUserCanAssignFoundingMember(mockUser, mockStudent);
        verify(groupService).requireGroupNameNotDuplicate(mockSession, "New Test Group");
        verify(groupService).createGroup(mockSession, "New Test Group", mockStudent);
    }

    @Test
    void testModifyGroupPreferences_ValidRequest_ReturnsSuccess() throws Exception {
        // Arrange
        String sessionId = "session-123";
        String groupId = "group-123";
        String requestBody = """
                {
                    "name": "Updated Group Name",
                    "desiredGroupSizeMin": 3,
                    "desiredGroupSizeMax": 5,
                    "desiredProjectId1": "project-1",
                    "desiredProjectId2": "project-2",
                    "desiredProjectId3": "project-3"
                }
                """;

        when(mockGroup.getName()).thenReturn("Old Group Name");
        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);
        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);
        when(requestRequirementService.requireGroupExists(groupId))
                .thenReturn(mockGroup);

        // Act & Assert
        mockMvc.perform(post("/groups/{sessionId}/modifyGroupPreferences/{groupId}", 
                sessionId, groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(content().string("Group preferences succesfully modified"));

        verify(requestRequirementService).requireUserExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireGroupExists(groupId);
        verify(requestRequirementService).requireUserIsAuthorizedSession(sessionId, mockUser);
        verify(groupService).requireGroupNameNotDuplicate(mockSession, "Updated Group Name");
        verify(groupService).requireUserOwnsGroupOrIsCoordinator(mockGroup, mockUser);
    }

    @Test
    void testModifyGroupSupervisor_ValidCoordinator_ReturnsSuccess() throws Exception {
        // Arrange
        String sessionId = "session-123";
        String groupId = "group-123";
        String supervisorId = "supervisor-123";

        when(requestRequirementService.requireUserCoordinatorExists(any(HttpServletRequest.class)))
                .thenReturn(mockCoordinator);
        when(requestRequirementService.requireGroupExists(groupId))
                .thenReturn(mockGroup);
        when(requestRequirementService.requireSupervisorExists(supervisorId))
                .thenReturn(mockSupervisor);
        when(requestRequirementService.requireSessionExists(sessionId))
                .thenReturn(mockSession);

        // Act & Assert
        mockMvc.perform(post("/groups/{sessionId}/modifyGroupSupervisor/{groupId}/{supervisorId}", 
                sessionId, groupId, supervisorId)
                .sessionAttr("user", mockCoordinator))
                .andExpect(status().isOk())
                .andExpect(content().string("Group supervisor succesfully modified"));

        verify(requestRequirementService).requireUserCoordinatorExists(any(HttpServletRequest.class));
        verify(requestRequirementService).requireGroupExists(groupId);
        verify(requestRequirementService).requireSupervisorExists(supervisorId);
        verify(requestRequirementService).requireSessionExists(sessionId);
        verify(requestRequirementService).requireCoordinatorIsAuthorizedSession(sessionId, mockCoordinator);
    }

    @Test
    void testCancelJoinRequest_ValidStudent_ReturnsSuccess() throws Exception {
        // Arrange
        when(requestRequirementService.requireUserStudentExists(any(HttpServletRequest.class)))
                .thenReturn(mockStudent);
        doNothing().when(groupService).cancelJoinRequest(mockStudent);

        // Act & Assert
        mockMvc.perform(post("/groups/cancelJoinRequest")
                .sessionAttr("user", mockStudent))
                .andExpect(status().isOk())
                .andExpect(content().string("Group join request succesfully canceled"));

        verify(requestRequirementService).requireUserStudentExists(any(HttpServletRequest.class));
        verify(groupService).cancelJoinRequest(mockStudent);
    }

    @Test
    void testCreateGroup_InvalidName_ReturnsBadRequest() throws Exception {
        // Arrange
        String sessionId = "session-123";
        String requestBody = """
                {
                    "name": "",
                    "studentId": "student-123"
                }
                """;

        when(requestRequirementService.requireUserExists(any(HttpServletRequest.class)))
                .thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/groups/{sessionId}/createGroup", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .sessionAttr("user", mockUser))
                .andExpect(status().isBadRequest());
    }

    @Configuration
    static class TestConfig {
    }
}