import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { useCallback } from "react";
import { fetchWithDefaultErrorHandling } from "../utils/fetchHelpers";

async function fetchSessionProjects(sessionId) {
	const response = await fetchWithDefaultErrorHandling(
		`/sessions/${sessionId}/getProjects`,
		{
			credentials: "include",
			method: "GET"
		}
	);
	return await response.json();
}

export default function useGetSessionProjects(sessionId) {

	const [isLoading, setIsLoading] = useState(true); // create two pieces of memory, true because no data fetched yet
	const [projects, setProjects] = useState(null); // projects null because no data yet

	useEffect(() => {
		if (sessionId == null) return; // if no sessionId, do nothing

		(async () => { // fetch data from the server
			try { // try to fetch, catch will throw error if fail
				setProjects(await fetchSessionProjects(sessionId));
			} catch (error) {
				alert("Error fetching session projects: ", error);
			} finally {
				setIsLoading(false); // done loading, so set useState of isLoading to false
			}
		})();
	}, [sessionId]);

	return { isLoading, projects, setProjects };
}

export function useCreateProject() {
	return useCallback(async (sessionId, { name, description }) => {
		const body = { sessionId, name, description };
		const response = await fetchWithDefaultErrorHandling(
			`/project`,
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
			`/project/${id}`,
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
			`http://localhost:8080/project/${id}`,
			{
				credentials: "include",
				method: "DELETE"
			}
		);
		return await response.json();
	}, []);
}

export function useGetSessionProjectsByParam() {
	const { sessionId } = useParams();
	return useGetSessionProjects(sessionId);
}
