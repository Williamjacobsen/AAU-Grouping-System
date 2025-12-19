import React, { useState, useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import { useCallback } from "react";
import { fetchWithDefaultErrorHandling } from "utils/fetchHelpers";

async function fetchSessionProjects(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/api/project/getSessionProjects/${sessionId}`,
		{
			credentials: "include",
			method: "GET"
		}
	);
	return await response.json();
}

export default function useGetSessionProjects(sessionId, pollingInterval) {

	const [isLoading, setIsLoading] = useState(true); // create two pieces of memory, true because no data fetched yet
	const [projects, setProjects] = useState(null); // projects null because no data yet
	const intervalRef = useRef(null);

	useEffect(() => {
		if (sessionId == null) return; // if no sessionId, do nothing

		const fetchData = async () => { // fetch data from the server
			try { // try to fetch, catch will throw error if fail
				setProjects(await fetchSessionProjects(sessionId));
			} catch (error) {
				alert("Error fetching session projects: ", error);
			} finally {
				setIsLoading(false); // done loading, so set useState of isLoading to false
			}
		};

		fetchData();

		if (pollingInterval) {
			intervalRef.current = setInterval(fetchData, pollingInterval);
		}

		return () => {
			if (intervalRef.current) {
				clearInterval(intervalRef.current);
			}
		};
	}, [sessionId]);

	return { isLoading, projects, setProjects };
}

export function useCreateProject() {
	return useCallback(async (sessionId, { name, description }) => {
		const body = { sessionId, name, description };
		const response = await fetchWithDefaultErrorHandling(
			`/api/project`,
			{
				credentials: "include",
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(body)
			}
		);
		return await response.json();
	}, []);
}

export function useUpdateProject() {
	return useCallback(async (id, { name, description }) => {
		const response = await fetchWithDefaultErrorHandling(
			`/api/project/${id}`,
			{
				credentials: "include",
				method: "PUT",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ name, description })
			}
		);
		return await response.json();
	}, []);
}

export function useDeleteProject() {
	return useCallback(async (id) => {
		const response = await fetchWithDefaultErrorHandling(
			`/api/project/${id}`,
			{
				credentials: "include",
				method: "DELETE"
			}
		);
		return await response.json();
	}, []);
}

export function useGetSessionProjectsByParam(pollingInterval) {
	const { sessionId } = useParams();
	return useGetSessionProjects(sessionId, pollingInterval);
}
