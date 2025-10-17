import React, { createContext, useContext, useEffect, useState } from "react";

const AppStateContext = createContext(null);

export function AppStateProvider({ children }) {
  const [students, setStudents] = useState(["student 1", "student 2"]);
  const [groups, setGroups] = useState(["group 1", "group 2"]);
  const [projects, setProjects] = useState(["project 1", "project 2"]);

  const [chatRooms, setChatRooms] = useState(() => 
    ["General", ...projects, ...groups, ...students]
  ); 
  useEffect(() => {
    setChatRooms(["General", ...projects, ...groups, ...students]);
  }, [projects, groups, students]);

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
