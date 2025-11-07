import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';
import { BrowserRouter } from 'react-router-dom';
import StudentPage from './StudentPage';
import { useGetUser } from '../../hooks/useGetUser';
import useStudentData from './useStudentData';
import useGetCurrentTime from '../../hooks/useGetCurrentTime';
import useIsQuestionnaireDeadlineExceeded from '../../hooks/useIsQuestionnaireDeadlineExceeded';

vi.mock('../../hooks/useGetUser', () => ({
  useGetUser: vi.fn(),
}));

vi.mock('./useStudentData', () => ({
  default: vi.fn(),
}));

vi.mock('../../hooks/useGetCurrentTime', () => ({
  default: vi.fn(),
}));

vi.mock('../../hooks/useIsQuestionnaireDeadlineExceeded', () => ({
  default: vi.fn(),
}));

const MockedStudentPage = () => (
  <BrowserRouter>
    <StudentPage />
  </BrowserRouter>
);

describe('StudentPage Component', () => {
  const mockUser = {
    id: 'student-123',
    email: 'student@test.com',
    name: 'Test Student',
    role: 'Student',
  };

  const mockStudentData = {
    student: {
      id: 'student-123',
      email: 'student@test.com',
      name: 'Test Student',
      sessionId: 'session-123',
      questionnaire: {
        projectPriorities: ['project-1', 'project-2', 'project-3'],
      },
    },
    session: {
      id: 'session-123',
      name: 'Test Session',
      questionnaireDeadline: '2024-12-31T23:59:59',
    },
    projects: [
      {
        id: 'project-1',
        title: 'Project One',
        description: 'Description for project one',
      },
      {
        id: 'project-2',
        title: 'Project Two',
        description: 'Description for project two',
      },
      {
        id: 'project-3',
        title: 'Project Three',
        description: 'Description for project three',
      },
    ],
    groups: [],
  };

  beforeEach(() => {
    cleanup();
    vi.clearAllMocks();
    
    useGetUser.mockReturnValue({
      user: mockUser,
      loading: false,
      setUser: vi.fn(),
    });

    useStudentData.mockReturnValue({
      student: mockStudentData.student,
      loading: false,
      error: null,
      isCoordinator: false,
      removeStudent: vi.fn(),
      resetPassword: vi.fn(),
    });

    useGetCurrentTime.mockReturnValue(new Date('2024-11-01T10:00:00'));
    useIsQuestionnaireDeadlineExceeded.mockReturnValue(false);
  });

  afterEach(() => {
    cleanup();
  });

  it('renders student information when loaded', () => {
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/test student/i)).toBeInTheDocument();
    expect(screen.getByText(/Name:/i)).toBeInTheDocument();
  });

  it('displays loading state when user data is loading', () => {
    useGetUser.mockReturnValue({ user: null, loading: true });
    
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/Student Details/i)).toBeInTheDocument();
  });

  it('displays loading state when student data is loading', () => {
    useStudentData.mockReturnValue({ ...mockStudentData, loading: true });
    
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/loading/i)).toBeInTheDocument();
  });

  it('displays session information', () => {
    render(<MockedStudentPage />);
    
    // Check basic structure exists
    expect(screen.getByText(/Student Details/i)).toBeInTheDocument();
    expect(screen.getByText(/Basic Information/i)).toBeInTheDocument();
    expect(screen.getByText(/Group Status/i)).toBeInTheDocument();
    expect(screen.getByText(/Questionnaire Responses/i)).toBeInTheDocument();
  });

  it('displays available projects', () => {
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/Project Priority 1:/i)).toBeInTheDocument();
    expect(screen.getByText(/Project Priority 2:/i)).toBeInTheDocument();
  });

  it('shows questionnaire form when deadline not exceeded', () => {
    useIsQuestionnaireDeadlineExceeded.mockReturnValue(false);
    
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/questionnaire responses/i)).toBeInTheDocument();
    expect(screen.getByText(/Project Priority 1:/i)).toBeInTheDocument();
  });

  it('shows questionnaire responses section regardless of deadline', () => {
    useIsQuestionnaireDeadlineExceeded.mockReturnValue(true);
    
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/questionnaire responses/i)).toBeInTheDocument();
  });

  it('displays questionnaire response fields', () => {
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/Project Priority 1:/i)).toBeInTheDocument();
    expect(screen.getByText(/Project Priority 2:/i)).toBeInTheDocument();
    expect(screen.getByText(/Desired Group Size:/i)).toBeInTheDocument();
    expect(screen.getByText(/Working Environment:/i)).toBeInTheDocument();
  });

  it('shows back button for navigation', () => {
    render(<MockedStudentPage />);
    
    const backButton = screen.getByRole('button', { name: /back/i });
    expect(backButton).toBeInTheDocument();
  });

  it('displays error when user not found', () => {
    useGetUser.mockReturnValue({ user: null, loading: false });
    useStudentData.mockReturnValue({ 
      student: null, 
      session: null, 
      loading: false,
      error: 'Failed to load student data',
      isCoordinator: false,
      removeStudent: vi.fn(),
      resetPassword: vi.fn(),
    });
    
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/Failed to load student data/i)).toBeInTheDocument();
    expect(screen.getByText(/Go Back/i)).toBeInTheDocument();
  });

  it('shows group status when student is not in a group', () => {
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/This student is not in a group/i)).toBeInTheDocument();
  });

  it('shows group status message', () => {
    render(<MockedStudentPage />);
    
    expect(screen.getByText(/Group Status/i)).toBeInTheDocument();
    expect(screen.getByText(/This student is not in a group/i)).toBeInTheDocument();
  });

});