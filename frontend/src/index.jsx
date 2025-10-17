import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import reportWebVitals from "./reportWebVitals";
import "./index.css";

import NoPage from "./pages/NoPage/NoPage";
import Header from "./pages/Header/Header";
import About from "./pages/About/About";
import SignIn from "./pages/User/SignIn";
import SignUp from "./pages/User/SignUp";
import Profile from "./pages/User/Profile";
import Status from "./pages/Status/Status";
import Sessions from "./pages/Sessions/Sessions";
import Projects from "./pages/Projects/Projects";

export default function App() {
	return (
		<React.StrictMode>
			<BrowserRouter>
				<Routes>
					<Route path="/" element={<Header />}>
						<Route index element={<About />} />
						<Route path="/sign-in" element={<SignIn />} />
						<Route path="/sign-up" element={<SignUp />} />
						<Route path="/profile" element={<Profile />} />
						<Route path="/sessions" element={<Sessions />}/>
						<Route path="/session/:id" element={<Status />} />
						<Route path="/session/:id/projects" element={<Projects />}/>
						<Route path="*" element={<NoPage />} />
					</Route>
				</Routes>
			</BrowserRouter>
		</React.StrictMode>
	);
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();



					