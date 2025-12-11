import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route, Outlet } from "react-router-dom";
import reportWebVitals from "./reportWebVitals";
import "./index.css";

import NoPage from "./pages/NoPage/NoPage";
import Header from "./Components/Header/Header";
import About from "./pages/About/About";
import SignIn from "./pages/User/SignIn";
import SignUp from "./pages/User/SignUp";
import Profile from "./pages/User/Profile";
import ForgotPassword from "./pages/User/ForgotPassword";
import ResetPassword from "./pages/User/ResetPassword";
import Students from "./pages/Status/Status";
import Sessions from "./pages/Sessions/Sessions";
import Projects from "./pages/Projects/Projects";
import StudentQuestionnaire from "./pages/StudentQuestionnaire/StudentQuestionnaire";
import GroupManagement from "./pages/GroupManagement/GroupManagement";
import SessionSetup from "./pages/SessionSetup/SessionSetup";
import SupervisorsPage from "./pages/SupervisorsPage/SupervisorsPage";
import StudentPage from "./pages/StudentPage/StudentPage";
import ChatBox from "./Components/ChatBox/ChatBox";
import MyGroup from "./pages/MyGroup/MyGroup";
import { AppStateProvider } from "./ContextProviders/AppStateContext";
import { AuthProvider } from "./ContextProviders/AuthProvider";

export default function App() {
	return (
		<React.StrictMode>
			<AuthProvider>
				<BrowserRouter>
					<Routes>
						<Route path="/" element={<Header />}>
							<Route path="/about" element={<About />} />
							<Route path="sign-in" element={<SignIn />} />
							<Route path="sign-up" element={<SignUp />} />
							<Route path="profile" element={<Profile />} />
							<Route path="forgotPassword" element={<ForgotPassword />} />
							<Route path="resetPassword" element={<ResetPassword />} />
							<Route path="sessions" element={<Sessions />} />
							<Route
								path="session/:sessionId"
								element={
									<AppStateProvider>
										<Outlet />
									</AppStateProvider>
								}
							>
								<Route
									element={
										<>
											<Outlet />
											<ChatBox />
										</>
									}
								>
									<Route path="setup" element={<SessionSetup />} />
									<Route path="projects" element={<Projects />} />
									<Route path="students" element={<Students />} />
									<Route path="my-group" element={<MyGroup />} />
									<Route path="groupManagement" element={<GroupManagement />} />
									<Route path="studentQuestionnaire" element={<StudentQuestionnaire />} />
									<Route path="supervisorsPage" element={<SupervisorsPage />} />
									<Route path="student/:studentId" element={<StudentPage />} />
								</Route>
							</Route>
							<Route path="*" element={<NoPage />} />
						</Route>
					</Routes>
				</BrowserRouter>
			</AuthProvider>
		</React.StrictMode>
	);
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
