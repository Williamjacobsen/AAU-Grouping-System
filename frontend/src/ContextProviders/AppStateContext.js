import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  useMemo,
} from "react";

import { useGetSessionByParameter } from "../hooks/useGetSession";
import { useGetSessionGroupsByParam } from "../hooks/useGetSessionGroups";
import { useGetSessionProjectsByParam } from "../hooks/useGetSessionProjects";
import { useGetSessionStudentsByParam } from "../hooks/useGetSessionStudents";
import { useGetSessionSupervisorsByParam } from "../hooks/useGetSessionSupervisors";
import useIsQuestionnaireDeadlineExceeded from "../hooks/useIsQuestionnaireDeadlineExceeded";

import { useAuth } from "./AuthProvider";

const AppStateContext = createContext(null);

export function AppStateProvider({ children }) {
  const { user } = useAuth();

  const { isLoading: loadingSession, session: sessionData } =
    useGetSessionByParameter();
  const { isLoading: loadingProjects, projects: projectData } =
    useGetSessionProjectsByParam();
  const { isLoading: loadingGroups, groups: groupData } =
    useGetSessionGroupsByParam();
  const { isLoading: loadingStudents, students: studentData } =
    useGetSessionStudentsByParam();
  const { isLoading: loadingSupervisors, supervisors: supervisorData } =
    useGetSessionSupervisorsByParam();

  const [session, setSession] = useState(null);
  const [projects, setProjects] = useState([]);
  const [groups, setGroups] = useState([]);
  const [students, setStudents] = useState([]);
  const [supervisors, setSupervisors] = useState([]);

  useEffect(() => {
    if (sessionData) setSession(sessionData);
  }, [sessionData]);

  useEffect(() => {
    if (projectData) setProjects(projectData);
  }, [projectData]);

  useEffect(() => {
    if (groupData) setGroups(groupData);
  }, [groupData]);

  useEffect(() => {
    if (studentData) setStudents(studentData);
  }, [studentData]);

  useEffect(() => {
    if (supervisorData) setSupervisors(supervisorData);
  }, [supervisorData]);

  useEffect(() => {
    setSession(null);
    setProjects([]);
    setGroups([]);
    setStudents([]);
    setSupervisors([]);
  }, [user?.id]);

  const isLoading =
    loadingSession ||
    loadingProjects ||
    loadingGroups ||
    loadingStudents ||
    loadingSupervisors;

  const chatRooms = useMemo(() => {
    return [
      "General",
      ...projects.map((p) => p.name),
      ...groups.map((g) => g.name),
      ...students.map((s) => s.name),
    ].filter(Boolean);
  }, [projects, groups, students]);

  const value = {
    projects,
    groups,
    students,
    supervisors,
    chatRooms,
    isLoading,
    session,

    setSession,
    setProjects,
    setGroups,
    setStudents,
    setSupervisors,

    isDeadlineExceeded:
      useIsQuestionnaireDeadlineExceeded(session).isDeadlineExceeded,
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
