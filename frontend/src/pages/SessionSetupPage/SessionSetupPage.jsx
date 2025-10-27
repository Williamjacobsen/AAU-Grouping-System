import React, { useState } from "react";

export default function SessionSetupPage() {
  const [form, setForm] = useState({
    sessionName: "",
    coordinatorName: "",
    description: "",
    numberOfStudents: "",
    maxGroupSize: "",
    minGroupSize: "",
    groupRounding: "none",
    studentEmails: "",
    supervisors: "",
    questionnaireDeadline: "",
    initialProjects: "",
    optionalQuestion: "",
    sendOnlyNew: true,
  });

  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === "checkbox" ? checked : value });
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setMessage("Saving session...");

    try {
      const res = await fetch("http://localhost:8080/sessions", {
        method: "POST",
        headers: { "Content-Type": "application/json" }, 
        credentials: "include",
        body: JSON.stringify(form),
      });

      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      setMessage("Session created successfully!");
      handleReset();
    } catch (err) {
      setMessage("Failed to create session: " + err.message);
    }
  };

  const handleReset = () => {
    setForm({
      sessionName: "",
      coordinatorName: "",
      description: "",
      numberOfStudents: "",
      maxGroupSize: "",
      minGroupSize: "",
      groupRounding: "none",
      studentEmails: "",
      supervisors: "",
      questionnaireDeadline: "",
      initialProjects: "",
      optionalQuestion: "",
      sendOnlyNew: true,
    });
    setMessage("Form has been reset!");
  };

  return (
    <div style={{ maxWidth: 600, margin: "2rem auto", padding: "1rem" }}>
      <h2>Session Setup</h2>

      <form onSubmit={handleSave}>
        <label>
          Session Name*<br />
          <input
            name="sessionName"
            value={form.sessionName}
            onChange={handleChange}
            required
          />
        </label>
        <br /><br />

        <label>
          Coordinator Name*<br />
          <input
            name="coordinatorName"
            value={form.coordinatorName}
            onChange={handleChange}
            required
          />
        </label>
        <br /><br />

        <label>
          Description<br />
          <textarea
            name="description"
            value={form.description}
            onChange={handleChange}
          />
        </label>
        <br /><br />

        <label>
          Number of Students*<br />
          <input
            type="number"
            name="numberOfStudents"
            value={form.numberOfStudents}
            onChange={handleChange}
            required
          />
        </label>
        <br /><br />

        <label>
          Max Group Size*<br />
          <input
            type="number"
            name="maxGroupSize"
            value={form.maxGroupSize}
            onChange={handleChange}
            required
          />
        </label>
        <br /><br />

        <label>
          Min Group Size<br />
          <input
            type="number"
            name="minGroupSize"
            value={form.minGroupSize}
            onChange={handleChange}
          />
        </label>
        <br /><br />

        <label>
          Group Rounding<br />
          <select
            name="groupRounding"
            value={form.groupRounding}
            onChange={handleChange}
          >
            <option value="none">No rounding</option>
            <option value="round_up">Round up</option>
            <option value="round_down">Round down</option>
          </select>
        </label>
        <br /><br />

        <label>
          Emails of Students*<br />
          <textarea
            name="studentEmails"
            value={form.studentEmails}
            onChange={handleChange}
            required
          />
        </label>
        <br /><br />

        <label>
          Supervisors (email,maxGroups)*<br />
          <textarea
            name="supervisors"
            value={form.supervisors}
            onChange={handleChange}
            required
          />
        </label>
        <br /><br />

        <label>
          Questionnaire Deadline<br />
          <input
            type="date"
            name="questionnaireDeadline"
            value={form.questionnaireDeadline}
            onChange={handleChange}
          />
        </label>
        <br /><br />

        <label>
          Initial Projects<br />
          <textarea
            name="initialProjects"
            value={form.initialProjects}
            onChange={handleChange}
          />
        </label>
        <br /><br />

        <label>
          Optional Question<br />
          <textarea
            name="optionalQuestion"
            value={form.optionalQuestion}
            onChange={handleChange}
          />
        </label>
        <br /><br />

        <label>
          <input
            type="checkbox"
            name="sendOnlyNew"
            checked={form.sendOnlyNew}
            onChange={handleChange}
          />{" "}
          Send only to newly added participants
        </label>
        <br /><br />

        <button type="button">
          Send login code to students
        </button>

        <button
          type="button"
          style={{ marginLeft: "10px" }}
        >
          Send login code to supervisors
        </button>

        <br /><br />
        <button type="submit">Create Session</button>

        <button
          type="button"
          onClick={handleReset}
          style={{ marginLeft: "10px" }}
        >
          Reset
        </button>
      </form>

      {message && <p style={{ marginTop: "1rem" }}>{message}</p>}
    </div>
  );
}
