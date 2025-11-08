import React from "react";
import '@testing-library/jest-dom/vitest';
import { describe, test, expect } from "vitest";
import { render, screen } from "@testing-library/react";

import StudentTable from "./StudentTable"
import { BrowserRouter } from "react-router-dom";

describe(StudentTable.name, function () {

  test("Table content renders properly", async function () {

		// Mock list of students
		const mockStudents = [];
		mockStudents.push({ name: "Name A", group: { number: "2", project: "Project B" } });
		mockStudents.push({ name: "Name B", group: { number: "1", project: "Project A" } });
		mockStudents.push({ name: "Name C", group: { number: "2", project: "Project B" } });

		// Render component
		render(
			<BrowserRouter>
				<StudentTable students={mockStudents} />
			</BrowserRouter>
			);
		
		// Check if content exists
		const rows = screen.getAllByRole('row').slice(1); // slice(1) to skip header
		expect(rows).toHaveLength(mockStudents.length);
		mockStudents.forEach((student, index) => {
			const row = rows[index];
			expect(row).toHaveTextContent(student.name);
			expect(row).toHaveTextContent(student.group.number);
			expect(row).toHaveTextContent(student.group.project);
		});
  })
}) 