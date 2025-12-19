import { describe, test, expect, vi } from "vitest";
import { renderHook, waitFor } from "@testing-library/react";

import useGetSessionStudents from "./useGetSessionStudents";

describe(useGetSessionStudents.name, function () {

  test("Fetching an existing session and updating states", async function () {

		// Simplified mock session
		const mockSessionId = 10;
		const mockStudents = [
				{ email: "Student A email", name: "Student A name" },
				{ email: "Student B email", name: "Student B name" }
			]; 

		// Mock next HTTP response
    global.fetch = vi.fn(function () {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockStudents),
      })
    });

		// Render hook
		const { result } = renderHook(() => useGetSessionStudents(mockSessionId));

		// Check hook states at initialization
    expect(result.current.isLoading).toBe(true);
    expect(result.current.students).toBeNull();

		// Wait for hooks to fetch HTTP response, then check hook states
    await waitFor(function () {
      expect(result.current.isLoading).toBe(false);
			expect(result.current.students).toEqual(mockStudents);
			expect(result.current.students[1].name).toEqual("Student B name")
    });
  })
})