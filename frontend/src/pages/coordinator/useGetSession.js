import React, { useEffect } from "react";

// This is an example. We will likely delete this at some point.

export default function useGetSession(setSessions) {
	useEffect(() => {
		fetch("http://localhost:8080/coordinator", { method: "GET" })
			.then((res) => res.json())
			.then((data) => {
				console.log(data);
				setSessions(Object.values(data));
			})
			.catch((err) => console.error(err));
	}, []);
}
