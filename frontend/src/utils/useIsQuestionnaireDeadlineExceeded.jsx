import React, { useCallback } from "react";
import useGetCurrentTime from "./useGetCurrentTime";

export default function useIsQuestionnaireDeadlineExceeded(session) {

	const { currentTime } = useGetCurrentTime();

	const isDeadlineExceeded = useCallback(() => {
		if (!(session?.questionnaireDeadline)) {
			return false;
		}
		
		// "session.questionnaireDeadline" is an ISO string, so we can easily convert it to a Date object.
		const deadline = new Date(session.questionnaireDeadline);
		return deadline.getTime() < currentTime.getTime();
	}, [currentTime, session]);

	return { isDeadlineExceeded }

}
	