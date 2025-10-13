import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import reportWebVitals from "./reportWebVitals";
import Coordinator from "./pages/coordinator/coordinator";
import "./index.css";

import NoPage from "./pages/NoPage/NoPage";
import Header from "./pages/Header/Header";
import About from "./pages/About/About";
import User from "./pages/User/User";
import Session from "./pages/Session/Session";
import SessionSetupPage from "./pages/SessionSetupPage/SessionSetupPage";

export default function App() {
	return (
		<React.StrictMode>
			<BrowserRouter>
				<Routes>
					<Route path="/" element={<Header />}>
						<Route index element={<About />} />
						<Route path="/user" element={<User />} />
						<Route path="/coordinator" element={<Coordinator />} />
						<Route path="/session/:id" element={<Session />}/>
						<Route path="*" element={<NoPage />} />
						<Route path="/session-setup" element={<SessionSetupPage />} />
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
