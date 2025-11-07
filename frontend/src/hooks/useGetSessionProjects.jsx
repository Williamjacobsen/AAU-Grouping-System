import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";

import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers"

async function fetchSessionProjects(sessionId) {
	return await fetchWithDefaultErrorHandling(
		`/sessions/${sessionId}/getProjects`,
		{
			method: "GET"
		}
	);
}

export default function useGetSessionProjects(sessionId) {

  const [isLoading, setIsLoading] = useState(true); // create two pieces of memory, true because no data fetched yet
  const [projects, setProjects] = useState(null); // projects null because no data yet

  useEffect(() => {
    if (sessionId == null) return; // if no sessionId, do nothing

    (async () => { // fetch data from the server
			try { // try to fetch, catch will throw error if fail
				setProjects(await fetchSessionProjects(sessionId))
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
