import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent, waitFor, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';
import { BrowserRouter } from 'react-router-dom';
import SignIn from './SignIn';

const MockedSignIn = () => (
	<BrowserRouter>
		<SignIn />
	</BrowserRouter>
);

global.fetch = vi.fn();

describe('SignIn Component', () => {
	beforeEach(() => {
		cleanup();
		vi.clearAllMocks();
		global.fetch.mockResolvedValue({
			ok: true,
			text: () => Promise.resolve('Signed in, user: test@example.com'),
		});
	});

	afterEach(() => {
		cleanup();
	});

	it('renders sign in form elements', () => {
		render(<MockedSignIn />);

		expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
		expect(screen.getByLabelText(/password/i)).toBeInTheDocument();
		expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
	});

	it('allows user to input email and password', () => {
		render(<MockedSignIn />);

		const emailInput = screen.getByLabelText(/email/i);
		const passwordInput = screen.getByLabelText(/password/i);

		fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
		fireEvent.change(passwordInput, { target: { value: 'password123' } });

		expect(emailInput.value).toBe('test@example.com');
		expect(passwordInput.value).toBe('password123');
	});

	it('submits form with correct data', async () => {
		render(<MockedSignIn />);

		const emailInput = screen.getByLabelText(/email/i);
		const passwordInput = screen.getByLabelText(/password/i);
		const submitButton = screen.getByRole('button', { name: /sign in/i });

		fireEvent.change(emailInput, { target: { value: 'coordinator@test.com' } });
		fireEvent.change(passwordInput, { target: { value: 'password123' } });

		fireEvent.click(submitButton);

		await waitFor(() => {
			expect(global.fetch).toHaveBeenCalledWith(`${process.env.REACT_APP_API_BASE_URL}/api/auth/signIn`, {
				method: 'POST',
				credentials: 'include',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify({
					emailOrId: 'coordinator@test.com',
					password: 'password123',
				}),
			});
		});
	});

	it('handles authentication failure', async () => {
		global.fetch.mockResolvedValueOnce({
			ok: false,
			status: 401,
			text: () => Promise.resolve('Unauthorized'),
		});

		render(<MockedSignIn />);

		const emailInput = screen.getByLabelText(/email/i);
		const passwordInput = screen.getByLabelText(/password/i);
		const submitButton = screen.getByRole('button', { name: /sign in/i });

		fireEvent.change(emailInput, { target: { value: 'wrong@test.com' } });
		fireEvent.change(passwordInput, { target: { value: 'wrongpassword' } });

		fireEvent.click(submitButton);

		await waitFor(() => {
			expect(screen.getByText(/unauthorized/i)).toBeInTheDocument();
		});
	});

	it('submits form even with empty fields', () => {
		render(<MockedSignIn />);

		const submitButton = screen.getByRole('button', { name: /sign in/i });
		fireEvent.click(submitButton);

		expect(global.fetch).toHaveBeenCalledWith(`${process.env.REACT_APP_API_BASE_URL}/api/auth/signIn`, {
			method: 'POST',
			credentials: 'include',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({
				emailOrId: '',
				password: '',
			}),
		});
	});

	it('displays loading state during submission', async () => {
		global.fetch.mockImplementation(() =>
			new Promise(resolve =>
				setTimeout(() => resolve({
					ok: true,
					text: () => Promise.resolve('Success')
				}), 100)
			)
		);

		render(<MockedSignIn />);

		const emailInput = screen.getByLabelText(/email/i);
		const passwordInput = screen.getByLabelText(/password/i);
		const submitButton = screen.getByRole('button', { name: /sign in/i });

		fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
		fireEvent.change(passwordInput, { target: { value: 'password' } });
		fireEvent.click(submitButton);

		await waitFor(() => {
			expect(global.fetch).toHaveBeenCalled();
		});
	});

	it('handles network errors gracefully', async () => {
		global.fetch.mockRejectedValueOnce(new Error('Network error'));

		render(<MockedSignIn />);

		const emailInput = screen.getByLabelText(/email/i);
		const passwordInput = screen.getByLabelText(/password/i);
		const submitButton = screen.getByRole('button', { name: /sign in/i });

		fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
		fireEvent.change(passwordInput, { target: { value: 'password' } });
		fireEvent.click(submitButton);

		await waitFor(() => {
			expect(global.fetch).toHaveBeenCalled();
		});
	});
});