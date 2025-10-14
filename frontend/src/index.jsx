import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import reportWebVitals from "./reportWebVitals";
import "./index.css";

import NoPage from "./pages/NoPage/NoPage";
import Header from "./pages/Header/Header";
import ChatBox from "./Components/ChatBox/ChatBox";
import { AppStateProvider } from "./AppStateContext";

export default function App() {
  return (
    <React.StrictMode>
      <BrowserRouter>
        <AppStateProvider>
          <Routes>
            <Route path="/" element={<Header />}>
              <Route path="/chatBoxTestRoute" element={<ChatBox />} />
              <Route path="*" element={<NoPage />} />
            </Route>
          </Routes>
        </AppStateProvider>
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
