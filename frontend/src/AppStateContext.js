import React, { createContext, useContext, useEffect, useState } from "react";

const AppStateContext = createContext(null);

export function AppStateProvider({ children }) {
  const [students, setStudents] = useState(["user_1", "user_2"]);
  const [groups, setGroups] = useState(["group 1", "group 2"]);
  const [projects, setProjects] = useState(["project 1", "project 2"]);

  const [chatRooms, setChatRooms] = useState(["General"]);
  useEffect(() => {
    setChatRooms(["General", ...projects, ...groups, ...students]);
  }, [groups, projects]);

  const value = {
    groups,
    setGroups,
    students,
    setStudents,
    projects,
    setProjects,
    chatRooms,
  };
  return (
    <AppStateContext.Provider value={value}>
      {children}
    </AppStateContext.Provider>
  );
}

export function useAppState() {
  const ctx = useContext(AppStateContext);
  if (!ctx) {
    throw new Error("useAppState must be used within <AppStateProvider>");
  }
  return ctx;
}
