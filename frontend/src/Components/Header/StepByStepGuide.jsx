import React from "react";
import { Link } from "react-router-dom";

export default function StepByStepGuide({ user, session, location, isLoadingSession }) {

	// Helper to build a link to a session page. Return "#" if session?.id is not available.
	function sessionLink(path) {
		if (session?.id) return `/session/${session.id}/${path}`;
		return "#";
	}

	// Each step has: id, label, to (link target) and a match string used to see current step.
	const guestSteps = [
		{ id: 1, label: "Read about this website", to: "/about", match: "/about" },
		{ id: 2, label: "Sign in, or create a coordinator account", to: "/sign-in", match: "/sign-in" },
	];

	const studentSteps = [
		{ id: 1, label: "Specify your preferences", to: sessionLink("studentQuestionnaire"), match: "studentQuestionnaire" },
		{ id: 2, label: "View available projects", to: sessionLink("projects"), match: "projects" },
		{ id: 3, label: "View and chat with students", to: sessionLink("students"), match: "students" },
		{ id: 3, label: "View your group", to: sessionLink("my-group"), match: "my-group" },
	];

	const supervisorSteps = [
		{ id: 1, label: "Add project proposals", to: sessionLink("projects"), match: "projects" },
	];

	const coordinatorSteps = [
		{ id: 1, label: "Create and edit a session", to: "/sessions", match: "sessions" },
		{ id: 2, label: "Set up the session and send login codes", to: sessionLink("setup"), match: "setup" },
		{ id: 3, label: "Configure max groups per supervisor", to: sessionLink("supervisorsPage"), match: "supervisorsPage" },
		{ id: 4, label: "Add project proposals", to: sessionLink("projects"), match: "projects" },
		{ id: 5, label: "Finalize groups", to: sessionLink("groupManagement"), match: "groupManagement" },
	];

	// Pick steps according to role
	let steps;
	if (!user) {
		steps = guestSteps;
	}
	else if (user.role === "Coordinator") {
		steps = coordinatorSteps;
	} else if (user.role === "Student") {
		steps = studentSteps;
	} else if (user.role === "Supervisor") {
		steps = supervisorSteps;
	}

	return (
		<div className="menu step-by-step-guide">
			<ul className="items">
				<li className="explanation">
					<b>
						Or follow this step-by-step guide:
					</b>
				</li>

				{steps.map(function (step) {
					// active when current pathname contains the match string
					const isActive = location.pathname && location.pathname.includes(step.match);

					// while session is loading do not render links for session-specific steps
					const showAsText = isLoadingSession || step.to === "#";

					return (
						<>
							<li key={step.id} >
								{showAsText ? (
									<span>{step.label}</span>
								) : (
									<Link
										to={step.to}
										className={isActive ? "active" : ""}
									>
										{step.label}
									</Link>
								)}
							</li>
							{step !== steps[steps.length - 1] &&
								<li>
									--{'>'}
								</li >
							}
						</>
					);
				})}
			</ul>
		</div >
	);
}
