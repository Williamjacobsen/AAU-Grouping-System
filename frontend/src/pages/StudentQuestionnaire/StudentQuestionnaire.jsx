import React, { useEffect, useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import { useGetUser } from "../../utils/useGetUser";

export default function StudentQuestionnaire() {

	const { isLoading: isLoadingUser, user } = useGetUser();
	
	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return <>Access denied: Not logged in.</>;
	if (user.role != "Student") return<>Access denied: Not a student.</>

	return (
		<>
			
		</>
	);
}