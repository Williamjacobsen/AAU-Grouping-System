import React, { useState, useEffect } from "react";

export default function useGetCurrentTime(updateIntervalInMs = 1000) {
	
	const [currentTime, setCurrentTime] = useState(new Date());

	useEffect(() => {
		const timer = setInterval(() => {
			setCurrentTime(new Date());
		}, updateIntervalInMs);

		return () => clearInterval(timer);
	}, []);

	return { currentTime };
}