import React from "react";
import "../Status/Status.css";

import { useAuth } from "../../ContextProviders/AuthProvider";
import { useAppState } from "ContextProviders/AppStateContext";

import GroupMenu from "./GroupMenu";

export default function Status() {
  // Get hooks
  const { isLoading: isLoadingUser, user } = useAuth();
  const {
    isLoading: isLoadingApp,
    session,
    students,
    projects,
    groups,
  } = useAppState();

  // Loading
  if (isLoadingUser)
    return <div className="loading-message">Checking authentication...</div>;
  if (!user)
    return (
      <div className="access-denied-message">Access denied: Not logged in.</div>
    );
  if (isLoadingApp)
    return <div className="loading-message">Loading information...</div>;

  return (
    <div className="status-container">
      <h1 className="status-title">My Group Status</h1>
      <div>
        <b>Students are allowed to make changes until this deadline: </b>
        {session?.questionnaireDeadline?.replace("T", " ") ?? "No deadline set"}
      </div>
      <div>
        <GroupMenu
          session={session}
          user={user}
          groups={groups}
          projects={projects}
          students={students}
        />
      </div>
    </div>
  );
}
