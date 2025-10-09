import React, { createContext, useContext, useState } from "react";

const AppStateContext = createContext(null);

export function AppStateProvider({ children }) {
	const [students, setStudents] = useState(["user_1", "user_2"]);
  const [groups, setGroups] = useState(["group_1", "group_2"]);

  const value = { groups, setGroups, students, setStudents };
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
