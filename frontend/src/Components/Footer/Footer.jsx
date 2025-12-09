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
    { id: 1, label: "1. Sign in", to: "/sign-in", match: "/sign-in" },
    { id: 2, label: "2. Profile", to: "/profile", match: "/profile" },
    { id: 3, label: "3. Questionnaire", to: sessionLink("studentQuestionnaire"), match: "studentQuestionnaire" },
    { id: 4, label: "4. Status", to: sessionLink("status"), match: "/status" },
  ];

  const coordinatorSteps = [
    { id: 1, label: "1. Sign in", to: "/sign-in", match: "/sign-in" },
    { id: 2, label: "2. Session Management", to: "/sessions", match: "/sessions" },
    { id: 3, label: "3. Create Session", to: "/sessions", match: "/sessions" },
    { id: 4, label: "4. Setup Page", to: sessionLink("setup"), match: "/setup" },
    { id: 5, label: "5. Supervisors Page", to: sessionLink("supervisorsPage"), match: "supervisorsPage" },
    { id: 6, label: "6. Status page", to: sessionLink("status"), match: "/status" },
    { id: 7, label: "7. Group management page", to: sessionLink("groupManagement"), match: "groupManagement" },
  ];

  // Pick steps according to role
  let steps;
  if (user.role === "Coordinator") {
    steps = coordinatorSteps;
  } else {
    steps = studentSteps;
  }

  return (
    <div className="app-footer">
      <ul className="footer-steps">
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
