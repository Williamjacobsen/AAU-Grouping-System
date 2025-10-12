import React, { useState } from 'react';

export default function SessionSetupPage() {
  const [sessionName, setSessionName] = useState('');
  const [description, setDescription] = useState('');
  const [coordinatorName, setCoordinatorName] = useState('');
  const [numberOfParticipants, setNumberOfParticipants] = useState('');
  const [maxGroupSize, setMaxGroupSize] = useState('');
  const [minGroupSize, setMinGroupSize] = useState('');
  const [groupRounding, setGroupRounding] = useState('none');
  const [studentEmails, setStudentEmails] = useState('');
  const [supervisorDetails, setSupervisorDetails] = useState('');
  const [questionnaireDeadline, setQuestionnaireDeadline] = useState('');
  const [initialProjects, setInitialProjects] = useState('');
  const [optionalQuestion, setOptionalQuestion] = useState('');
  const [sendOnlyNew, setSendOnlyNew] = useState(true);

  const handleSave = (e) => {
    e.preventDefault();
    const payload = {
      sessionName: sessionName.trim(),
      description: description.trim(),
      mandatory: {
        coordinatorName,
        numberOfParticipants: Number(numberOfParticipants) || null,
        maxGroupSize: Number(maxGroupSize) || null,
        minGroupSize: Number(minGroupSize) || null,
        groupRounding,
        studentEmails: studentEmails.split(/[,\n]+/).map((s) => s.trim()).filter(Boolean),
        supervisorDetails: supervisorDetails.split(/[,\n]+/).map((s) => s.trim()).filter(Boolean),
        questionnaireDeadline: questionnaireDeadline || null,
        initialProjects: initialProjects.split(/[,\n]+/).map((s) => s.trim()).filter(Boolean),
      },
      optional: optionalQuestion.trim() || null,
      meta: { createdAt: new Date().toISOString() },
    };
    console.log('Saving session setup:', payload);
    alert('Session saved');
  };

  const handleSendCodes = () => {
    if (!sessionName.trim()) return alert('Please enter a session name first.');
    const msg = sendOnlyNew
      ? `Sending login codes only to new participants for '${sessionName}'.`
      : `Sending login codes to all participants for '${sessionName}'.`;
    console.log(msg);
    alert(msg);
  };

  const handleReset = () => {
    [
      setSessionName,
      setDescription,
      setCoordinatorName,
      setNumberOfParticipants,
      setMaxGroupSize,
      setMinGroupSize,
      setStudentEmails,
      setSupervisorDetails,
      setQuestionnaireDeadline,
      setInitialProjects,
      setOptionalQuestion,
    ].forEach((fn) => fn(''));
    setGroupRounding('none');
    setSendOnlyNew(true);
  };

  return (
    <div>
      <h2>Session Setup</h2>
      <form onSubmit={handleSave}>
        {[
          ['Session Name*', sessionName, setSessionName, 'text', 'e.g. Fall 2025 P3', true],
          ['Coordinator Name*', coordinatorName, setCoordinatorName, 'text', 'Name of coordinator', true],
          ['Description', description, setDescription, 'textarea', '', false],
          ['Number of Students*', numberOfParticipants, setNumberOfParticipants, 'number', 'e.g. 120', true],
          ['Max Group Size*', maxGroupSize, setMaxGroupSize, 'number', 'e.g. 4', true],
          ['Min Group Size', minGroupSize, setMinGroupSize, 'number', 'e.g. 2', false],
        ].map(([label, val, setter, type, ph, req], i) => (
          <label key={i}>
            {label}
            <br />
            {type === 'textarea' ? (
              <textarea value={val} onChange={(e) => setter(e.target.value)} rows={3} placeholder={ph} />
            ) : (
              <input
                type={type}
                value={val}
                onChange={(e) => setter(e.target.value)}
                required={req}
                placeholder={ph}
                min={type === 'number' ? 1 : undefined}
              />
            )}
            <br />
          </label>
        ))}

        <label>
          Group Rounding
          <br />
          <select value={groupRounding} onChange={(e) => setGroupRounding(e.target.value)}>
            <option value="none">No rounding</option>
            <option value="round_up">Round up</option>
            <option value="round_down">Round down</option>
          </select>
        </label>

        {[
          ['Emails of Students*', studentEmails, setStudentEmails, 'e.g. s1@aau.dk, s2@aau.dk'],
          ['Supervisors (email,maxGroups)*', supervisorDetails, setSupervisorDetails, 'super1@aau.dk,2'],
          ['Questionnaire Deadline', questionnaireDeadline, setQuestionnaireDeadline, '', 'datetime-local'],
          ['Initial Projects', initialProjects, setInitialProjects, 'Project A, Project B'],
          ['Optional Question', optionalQuestion, setOptionalQuestion, 'Anything else?'],
        ].map(([label, val, setter, ph, type = 'textarea'], i) => (
          <label key={label}>
            <br />
            {label}
            <br />
            {type === 'datetime-local' ? (
              <input type="datetime-local" value={val} onChange={(e) => setter(e.target.value)} />
            ) : (
              <textarea value={val} onChange={(e) => setter(e.target.value)} rows={3} placeholder={ph} />
            )}
          </label>
        ))}

        <br />
        <button type="submit">Save Changes</button>
      </form>

      <hr />
      <label>
        <input type="checkbox" checked={sendOnlyNew} onChange={(e) => setSendOnlyNew(e.target.checked)} /> Send only
        to newly added participants
      </label>
      <br />
      <button onClick={handleSendCodes}>Send Login Codes</button>
      <button onClick={handleReset} style={{ marginLeft: 8 }}>
        Reset
      </button>
    </div>
  );
}
