import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../../ContextProviders/AuthProvider";
import { useGetSessionByUserOrParameter } from "../../hooks/useGetSession";

import "./Footer.css";

export default function Footer() {
  const { user } = useAuth();
  const location = useLocation();
  const { isLoading: isLoadingSession, session } = useGetSessionByUserOrParameter(user);

  // Get a session id if possible. Prefer the resolved session, otherwise use the id attached to user.
  let sessionId = null;
  if (session && session.id) sessionId = session.id;
  else if (user && user.sessionId) sessionId = user.sessionId;

  // If there is no logged-in user, don't render the footer.
  if (!user) {
    return null;
  }

  // Helper to build a link to a session page. Return "#" if sessionId is not available.
  function sessionLink(path) {
    if (sessionId) return `/session/${sessionId}/${path}`;
    return "#";
  }

  // Each step has: id, label, to (link target) and a match string used to see current step.
  const studentSteps = [
    { id: 1, label: "1. Specify your preferences", to: sessionLink("studentQuestionnaire"), match: "studentQuestionnaire" },
    { id: 2, label: "2. Find or create a group (also, use the chat box in the right corner)", to: sessionLink("status"), match: "/status" },
	];
	
	const supervisorSteps = [
    { id: 1, label: "1. Add project proposals", to: sessionLink("projects"), match: "projects" },
  ];

  const coordinatorSteps = [
    { id: 1, label: "1. Open or create a session", to: "/sessions", match: "/sessions" },
    { id: 2, label: "2. Set up the session", to: sessionLink("setup"), match: "/setup" },
		{ id: 3, label: "3. Configure supervisors", to: sessionLink("supervisorsPage"), match: "supervisorsPage" },
		{ id: 4, label: "4. Add project proposals (supervisors can also do this)", to: sessionLink("projects"), match: "projects" },
    { id: 5, label: "5. Finalize groups (after deadline is exceeded)", to: sessionLink("groupManagement"), match: "groupManagement" },
  ];

  // Pick steps according to role
  let steps;
  if (user.role === "Coordinator") {
    steps = coordinatorSteps;
  } else if (user.role === "Student") {
    steps = studentSteps;
  } else if (user.role === "Supervisor") {
    steps = supervisorSteps;
	} else {
		return null;
  }

  return (
		<div className="app-footer">
			<ul className="footer-steps">
				<b>Follow the steps:</b>
        {steps.map(function (step) {
          // active when current pathname contains the match string
          const active = location.pathname && location.pathname.includes(step.match);

          // while session is loading do not render links for session-specific steps
          const showAsText = isLoadingSession || step.to === "#";

          return (
            <li key={step.id} className={active ? "step active" : "step"}>
              {showAsText ? (
                <span className="step-link">{step.label}</span>
              ) : (
                <Link to={step.to} className="step-link">{step.label}</Link>
              )}
            </li>
          );
        })}
      </ul>
    </div>
  );
}
