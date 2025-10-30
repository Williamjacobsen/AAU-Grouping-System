import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

/**
 * @returns An object {isLoading, session}.
 * - isLoading is a useState boolean.
 * - projects is a useState Project array.
 */
export default function useGetSessionProjects(sessionId) {
  const [isLoading, setIsLoading] = useState(true); // create two pieces of memory, true because no data fetched yet
  const [projects, setProjects] = useState(null); // projects null because no data yet

  useEffect(() => {
    if (sessionId == null) return; // if no sessionId, do nothing

    (async () => { // fetch data from the server
			try { // try to fetch, catch will throw error if fail
				const response = await fetch(
					`http://localhost:8080/sessions/${sessionId}/getProjects`,
					{
						method: "GET",
						credentials: "include",
					}
				); // request to API to get projects

				if (!response.ok) {
					return Promise.reject("Status code " + response.status + ": " + await response.text());
				}

        const data = await response.json(); // parse from JSON to object
        setProjects(data); // store data
      } catch (error) {
        alert("Error fetching session projects: ", error);
      } finally {
        setIsLoading(false); // done loading, so set useState of isLoading to false
      }
    })();
  }, [sessionId]);

  return {isLoading, projects};
}

export function useGetSessionProjectsByParam() {
	const { sessionId } = useParams();
	return useGetSessionProjects(sessionId);
}
