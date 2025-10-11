import { describe, test, expect, vi } from "vitest";
import { renderHook, waitFor, render, screen } from "@testing-library/react";
import userEvent from '@testing-library/user-event'

import useStudentFiltering from "./useStudentFiltering"

describe(useStudentFiltering.name, function () {

	test("Entering search term updates filtering function functionality", async function () {

		// Mock list of students
		const mockStudents = [];
		mockStudents.push({ name: "Jonas", group: { number: "2", project: "Project B" } });
		mockStudents.push({ name: "Jytte", group: { number: "1", project: "Project A" } });
		mockStudents.push({ name: "John", group: { number: "2", project: "Project B" } });

		// Render hook
		const { result } = renderHook(() => useStudentFiltering());

		// Render component
		render(result.current.SearchFilterInput());
		
		// Check initial filtering  (when filter is empty)
		let filteredStudents = result.current.toFiltered(mockStudents);
		expect(filteredStudents).toBe(mockStudents)

		// Enter search term
		const searchInput = screen.getByRole("textbox");
		await userEvent.type(searchInput, "jo");

		// Wait for hook to update, then check filtering function has updated
    await waitFor(function () {
			filteredStudents = result.current.toFiltered(mockStudents);
			expect(filteredStudents.length).toBe(2)
    });
  })
}) 