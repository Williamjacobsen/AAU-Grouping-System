import { describe, test, expect, vi } from "vitest";
import { renderHook, waitFor } from "@testing-library/react";

import useGetSession from "./useGetSession";

describe(useGetSession.name, function () {

  test("Fetching an existing session and updating states", async function () {

		// Simplified mock session
		const mockSession = {
			ID: 10, students:
			[
				{ email: "Student A email", name: "Student A name" },
				{ email: "Student B email", name: "Student B name" }
			]
		}; 

		// Mock next HTTP response
    global.fetch = vi.fn(function () {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ session: mockSession }),
      })
    });

		// Render hook
		const { result } = renderHook(() => useGetSession());

		// Check hook states at initialization
    expect(result.current.isLoading).toBe(true);
    expect(result.current.session).toBeNull();

		// Wait for hooks to fetch HTTP response, then check hook states
    await waitFor(function () {
      expect(result.current.isLoading).toBe(false);
			expect(result.current.session).toEqual(mockSession);
			expect(result.current.session.students[1].name).toEqual("Student B name")
    });
  })
})