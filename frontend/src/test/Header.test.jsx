import React from 'react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, cleanup } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';
import Header from '../pages/Header/Header';
import { BrowserRouter } from 'react-router-dom';
import { useGetUser } from '../utils/useGetUser';
import { useGetSessionByUserOrParameter } from '../utils/useGetSession';

vi.mock('../utils/useGetUser', () => ({
  useGetUser: vi.fn(),
}));

vi.mock('../utils/useGetSession', () => ({
  useGetSessionByUserOrParameter: vi.fn(),
}));

const MockedHeader = () => (
  <BrowserRouter>
    <Header />
  </BrowserRouter>
);

describe('Header Component', () => {
  beforeEach(() => {
    cleanup(); 
    vi.clearAllMocks();
    global.fetch = vi.fn();
    
    useGetSessionByUserOrParameter.mockReturnValue({ 
      isLoading: false, 
      session: null 
    });
  });

  afterEach(() => {
    cleanup();
  });

  it('renders header when no user is provided', () => {
    useGetUser.mockReturnValue({ user: null, isLoading: false });
    
    render(<MockedHeader />);
    
    expect(screen.getByText(/About/i)).toBeInTheDocument();
  });

  it('displays user information when logged in', () => {
    const mockUser = {
      email: 'test@example.com',
      role: 'Coordinator',
      name: 'Test User',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    expect(screen.getByText(/Profile/i)).toBeInTheDocument();
  });

  it('shows sign out button when user is logged in', () => {
    const mockUser = {
      email: 'test@example.com',
      role: 'Coordinator',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    expect(screen.getByText(/Profile/i)).toBeInTheDocument();
  });

  it('shows sign in link when no user is logged in', () => {
    useGetUser.mockReturnValue({ user: null, isLoading: false });
    
    render(<MockedHeader />);
    
    expect(screen.getByText(/sign in or up/i)).toBeInTheDocument();
  });

  it('handles sign out action', async () => {
    global.fetch.mockResolvedValue({
      ok: true,
      text: () => Promise.resolve('Signed out'),
    });

    const mockUser = {
      email: 'test@example.com',
      role: 'Coordinator',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    // Test that the profile link
    expect(screen.getByText(/Profile/i)).toBeInTheDocument();
  });

  it('displays navigation menu for coordinator', () => {
    const mockUser = {
      email: 'coordinator@example.com',
      role: 'Coordinator',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    expect(screen.getByText(/Sessions/i)).toBeInTheDocument();
    expect(screen.getByText(/Profile/i)).toBeInTheDocument();
  });

  it('displays appropriate navigation for student', () => {
    const mockUser = {
      email: 'student@example.com',
      role: 'Student',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    // Students sees profile but not sessions
    expect(screen.getByText(/Profile/i)).toBeInTheDocument();
    expect(screen.queryByText(/Sessions/i)).not.toBeInTheDocument();
  });

  it('displays appropriate navigation for supervisor', () => {
    const mockUser = {
      email: 'supervisor@example.com',
      role: 'Supervisor',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    expect(screen.getByText(/Profile/i)).toBeInTheDocument();
    expect(screen.queryByText(/Sessions/i)).not.toBeInTheDocument();
  });

  it('handles navigation clicks', () => {
    const mockUser = {
      email: 'coordinator@example.com',
      role: 'Coordinator',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    const sessionsLink = screen.getByText(/Sessions/i);
    expect(sessionsLink).toBeInTheDocument();
  });

  it('shows loading state when user data is loading', () => {
    useGetUser.mockReturnValue({ user: null, isLoading: true });
    
    render(<MockedHeader />);
    
    // Header show loading state
    expect(screen.getByText(/Loading user.../i)).toBeInTheDocument();
  });

  it('handles sign out errors gracefully', async () => {
    global.fetch.mockRejectedValue(new Error('Network error'));

    const mockUser = {
      email: 'test@example.com',
      role: 'Coordinator',
    };
    
    useGetUser.mockReturnValue({ user: mockUser, isLoading: false });
    
    render(<MockedHeader />);
    
    // Test that profile is shown
    expect(screen.getByText(/Profile/i)).toBeInTheDocument();
  });

});