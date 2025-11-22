import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, cleanup, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';

import StudentPage from './StudentPage';
import { BrowserRouter, useParams, useNavigate } from 'react-router-dom';
import { useGetStudent } from '../../hooks/useGetStudent';
import { useAuth } from '../../ContextProviders/AuthProvider';
import { useAppState } from '../../ContextProviders/AppStateContext';
import { fetchWithDefaultErrorHandling } from '../../utils/fetchHelpers';

// Mock hooks

vi.mock('react-router-dom', () => ({
	useParams: vi.fn(),
	useNavigate: vi.fn(),
	BrowserRouter: ({ children }) => <div>{children}</div>,
}));
vi.mock('../../hooks/useGetStudent', () => ({
	useGetStudent: vi.fn(),
}));
vi.mock('../../ContextProviders/AuthProvider', () => ({
	useAuth: vi.fn(),
}));
vi.mock('../../ContextProviders/AppStateContext', () => ({
	useAppState: vi.fn(),
}));
vi.mock('../../utils/fetchHelpers', () => ({
	fetchWithDefaultErrorHandling: vi.fn(),
}));

// Mock data

const mockStudent = {
	id: 'studentId1',
	name: 'John Doe',
	email: 'john.doe@example.com',
	groupId: 'group1',
	questionnaire: {
		desiredProjectId1: 'project1',
		desiredProjectId2: 'project2',
		desiredProjectId3: 'project3',
		desiredGroupSizeMin: 200,
		desiredGroupSizeMax: 400,
		desiredWorkLocation: 'Remote',
		desiredWorkStyle: 'Together',
		personalSkills: 'JavaScript, React',
		academicInterests: 'Web Development',
		specialNeeds: 'None',
		comments: 'Looking forward to the project',
	},
};

const mockProjects = [
	{ id: 'project1', name: 'Project One' },
	{ id: 'project2', name: 'Project Two' },
	{ id: 'project3', name: 'Project Three' },
	{ id: 'project4', name: 'Project Four' },
	{ id: 'project5', name: 'Project Five' },
	{ id: 'project6', name: 'Project Six' },
];

const mockGroups = [
	{
		id: 'group1',
		name: "coolGroupName",
		studentIds: ['1', '2', '3'],
		desiredProjectId1: 'project4',
		desiredProjectId2: 'project5',
		desiredProjectId3: 'project6',
	},
];

const mockSession = {
	id: "sessionId1",
	maxGroupSize: 4,
};

const mockUserCoordinator = {
	role: 'Coordinator',
};

const mockUserStudent = {
	role: 'Student',
};

const MockedStudentPage = () => (
	<BrowserRouter>
		<StudentPage />
	</BrowserRouter>
);

// Tests

describe('StudentPage Component', () => {

	beforeEach(() => {
		// Clean up everything
		cleanup();
		vi.clearAllMocks();

		// Default mocking
		vi.mocked(useParams).mockReturnValue({
			studentId: mockStudent.id,
			sessionId: mockSession.id,
		});
		vi.mocked(useNavigate).mockReturnValue(vi.fn());
		useAuth.mockReturnValue({
			isLoading: false,
			user: mockUserCoordinator
		});
		useAppState.mockReturnValue({
			isLoading: false,
			projects: mockProjects,
			groups: mockGroups,
			session: mockSession,
			setStudents: vi.fn()
		});
		useGetStudent.mockReturnValue({
			isLoading: false,
			student: mockStudent
		});
	});

	afterEach(() => {
		cleanup();
	});

	it('renders student information when loaded', () => {
		render(<MockedStudentPage />);

		// Basic student info
		expect(screen.getByText(new RegExp(mockStudent.email, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockStudent.name, 'i'))).toBeInTheDocument();

		// Questionnaire - desired projects
		expect(screen.getByText(new RegExp(mockProjects[0].name, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockProjects[1].name, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockProjects[2].name, 'i'))).toBeInTheDocument();

		// Questionnaire - group size preferences
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.desiredGroupSizeMin.toString(), 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.desiredGroupSizeMax.toString(), 'i'))).toBeInTheDocument();

		// Questionnaire - work preferences
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.desiredWorkLocation, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.desiredWorkStyle, 'i'))).toBeInTheDocument();

		// Questionnaire - skills and interests
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.personalSkills, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.academicInterests, 'i'))).toBeInTheDocument();

		// Questionnaire - special needs and comments
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.specialNeeds, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockStudent.questionnaire.comments, 'i'))).toBeInTheDocument();

		// Group info
		expect(screen.getByText(new RegExp(mockGroups[0].name, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockProjects[3].name, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockProjects[4].name, 'i'))).toBeInTheDocument();
		expect(screen.getByText(new RegExp(mockProjects[5].name, 'i'))).toBeInTheDocument();
	});

	it('shows back button for navigation', () => {
		render(<MockedStudentPage />);

		const backButton = screen.getByRole('button', { name: /back/i });
		expect(backButton).toBeInTheDocument();
	});

	it('shows loading message when checking authentication', () => {
		useAuth.mockReturnValue({ isLoading: true, user: null });

		render(<MockedStudentPage />);

		expect(screen.getByText('Checking authentication...')).toBeInTheDocument();
	});

	it('shows confirmation popup when delete student button is clicked', () => {
		render(<MockedStudentPage />);

		const deleteButton = screen.getByText('Delete Student');
		fireEvent.click(deleteButton);

		expect(screen.getByText('Remove Student')).toBeInTheDocument();
		expect(screen.getByText('Delete the student from the session.')).toBeInTheDocument();
		expect(screen.getByText('This action cannot be undone.')).toBeInTheDocument();
	});

	it('shows confirmation popup when reset password button is clicked', () => {
		render(<MockedStudentPage />);

		const resetButton = screen.getByText('Send new password');
		fireEvent.click(resetButton);

		expect(screen.getByText('Reset Student Password')).toBeInTheDocument();
		expect(screen.getByText('Reset and email a new password to the student.')).toBeInTheDocument();
	});

	it('closes popup when cancel button is clicked', () => {
		render(<MockedStudentPage />);

		const deleteButton = screen.getByText('Delete Student');
		fireEvent.click(deleteButton);

		const cancelButton = screen.getByText('Cancel');
		fireEvent.click(cancelButton);

		expect(screen.queryByText('Remove Student')).not.toBeInTheDocument();
	});

	it('calls removeStudent when confirmation is confirmed', async () => {
		render(<MockedStudentPage />);

		const deleteButton = screen.getByText('Delete Student');
		fireEvent.click(deleteButton);

		const confirmButton = screen.getByText('Confirm');
		fireEvent.click(confirmButton);

		await waitFor(() => {
			expect(fetchWithDefaultErrorHandling).toHaveBeenCalledWith(
				`/student/${mockStudent.id}`,
				{
					method: 'DELETE',
					credentials: 'include',
				}
			);
		});
	});

	it('hides coordinator buttons when user is not coordinator', () => {
		useAuth.mockReturnValue({ isLoading: false, user: mockUserStudent });

		render(<MockedStudentPage />);

		expect(screen.queryByText('Send new password')).not.toBeInTheDocument();
		expect(screen.queryByText('Delete Student')).not.toBeInTheDocument();
	});

});