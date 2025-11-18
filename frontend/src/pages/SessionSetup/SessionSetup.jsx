import React, { useState } from "react";
import { useParams } from "react-router-dom";

import "./SessionSetup.css";
import SessionSetupForm from "./SessionSetupForm";
import SendLoginCodesForm from "./SendLoginCodesForm";
import ResetFormButton from "./ResetFormButton";
import NotifyButton from "../../Components/NotifyButton/NotifyButton";

import { useAppState } from "ContextProviders/AppStateContext";

export default function SessionSetup() {
  const { sessionId } = useParams(); // Gets the session ID via the URL parameter "../:sessionId/setup"
	
	const { isLoading, students, session, supervisors } = useAppState();

  const [message, setMessage] = useState("");

  if (isLoading)
    return <div className="loading-message">Loading information...</div>;

  return (
    <div className="session-setup-container">
      <h1 className="session-setup-title">Session Setup</h1>

      <SessionSetupForm
        sessionId={sessionId}
        session={session}
        supervisors={supervisors}
        students={students}
        setMessage={setMessage}
      />

      <ResetFormButton />

      <div>
        Do remember to go to the "Projects" menu and add project proposals.
      </div>

      <div className="form-section">
        <h3 className="section-title">Actions</h3>

        <SendLoginCodesForm
          sessionId={sessionId}
          targetUsers="supervisors"
          setMessage={setMessage}
        />

        <SendLoginCodesForm
          sessionId={sessionId}
          targetUsers="students"
          setMessage={setMessage}
        />

        <NotifyButton sessionId={sessionId} />
      </div>

      {message && <div className="message">{message}</div>}
    </div>
  );
}
