import React, { useState, useEffect } from "react";

/**
 * @returns An object {isLoading, session}.
 * - isLoading is a useState boolean.
 * - projects is a useState Project array.
 */
export default function useGetSessionProjects(sessionId) {
  const [isLoading, setIsLoading] = useState(true);
  const [projects, setProjects] = useState(null);

  useEffect(() => {
    if (sessionId == null) return;

    (async () => {
      try {
        const response = await fetch(`http://localhost:8080/project/getSessionProjects/${sessionId}`);
        if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);

        const data = await response.json();
        setProjects(data);
      } catch (error) {
        console.error("Error fetching session projects:", error);
      } finally {
        setIsLoading(false);
      }
    })();
  }, [sessionId]);

  return {isLoading, projects};
}
