import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route, Outlet } from "react-router-dom";
import reportWebVitals from "./reportWebVitals";
import "./index.css";

import NoPage from "./pages/NoPage/NoPage";
import Header from "./components/Header/Header";
import About from "./pages/About/About";
import SignIn from "./pages/profileSystem/SignIn";
import SignUp from "./pages/profileSystem/SignUp";
import Profile from "./pages/profileSystem/Profile";
import ForgotPassword from "./pages/profileSystem/ForgotPassword";
import ResetPassword from "./pages/profileSystem/ResetPassword";
import Students from "./pages/Students/Students";
import Sessions from "./pages/Sessions/Sessions";
import Projects from "./pages/Projects/Projects";
import MyWishes from "./pages/MyWishes/MyWishes";
import FinalizeGroups from "./pages/FinalizeGroups/FinalizeGroups";
import SessionSetup from "./pages/SessionSetup/SessionSetup";
import Supervisors from "./pages/Supervisors/Supervisors";
import Student from "./pages/Student/Student";
import ChatBox from "./components/ChatBox/ChatBox";
import MyGroup from "./pages/MyGroup/MyGroup";
import { AppStateProvider } from "./context/AppStateContext";
import { AuthProvider } from "./context/AuthProvider";

export default function App() {
	return (
		<React.StrictMode>
			<AuthProvider>
				<BrowserRouter>
					<Routes>
						<Route path="/" element={<Header />}>
							<Route path="about" element={<About />} />
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
									<Route path="finalize-groups" element={<FinalizeGroups />} />
									<Route path="my-wishes" element={<MyWishes />} />
									<Route path="supervisors" element={<Supervisors />} />
									<Route path="student/:studentId" element={<Student />} />
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
