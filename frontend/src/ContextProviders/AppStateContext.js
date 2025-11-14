import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  useMemo,
} from "react";
import { useParams } from "react-router-dom";
import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

const AppStateContext = createContext(null);

export function AppStateProvider({ children }) {
  const { sessionId } = useParams();

  const [students, setStudents] = useState([]);
  const [groups, setGroups] = useState([]);
  const [projects, setProjects] = useState([]);
  const [supervisors, setSupervisors] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!sessionId) return;
    (async () => {
      try {
        const [projectsRes, groupsRes, studentsRes, supervisorsRes] =
          await Promise.all([
            fetchWithDefaultErrorHandling(
              `/sessions/${sessionId}/getProjects`,
              { credentials: "include" }
            ),
            fetchWithDefaultErrorHandling(`/sessions/${sessionId}/getGroups`, {
              credentials: "include",
            }),
            fetchWithDefaultErrorHandling(
              `/sessions/${sessionId}/getStudents`,
              { credentials: "include" }
            ),
            fetchWithDefaultErrorHandling(
              `/sessions/${sessionId}/getSupervisors`,
              { credentials: "include" }
            ),
          ]).then((responses) => Promise.all(responses.map((r) => r.json())));

        setProjects(projectsRes);
        setGroups(groupsRes);
        setStudents(studentsRes);
        setSupervisors(supervisorsRes);
      } finally {
        setIsLoading(false);
      }
    })();
  }, [sessionId]);

  const chatRooms = useMemo(() => {
    return [
      "General",
      ...projects.map((p) => p.name),
      ...groups.map((g) => g.name),
      ...students.map((s) => s.name),
    ].filter(Boolean);
  }, [projects, groups, students]);

  const value = {
    students,
    setStudents,
    groups,
    setGroups,
    projects,
    setProjects,
    supervisors,
    setSupervisors,
    chatRooms,
    isLoading,
  };

  return (
    <AppStateContext.Provider value={value}>
      {children}
    </AppStateContext.Provider>
  );
}

export function useAppState() {
  const ctx = useContext(AppStateContext);
  if (!ctx)
    throw new Error("useAppState must be used within <AppStateProvider>");
  return ctx;
}
