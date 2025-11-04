import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useGetUser } from "../../utils/useGetUser";
import "./User.css";

export default function ForgotPassword() {

	const navigate = useNavigate();

		const [Email, setEmail] = useState("");
		const [error, setError] = useState("");
		const [succes, setSucces] = useState("");

	const handleEmailSubmit = async () => {
		try {
			const response = await fetch("http://localhost:8080/auth/forgotPassword", {
			method: "POST",
			headers: { "Content-Type": "application/json" },
			body: JSON.stringify({ Email }),
		})
		if (response.ok) {
			setSucces("An Email has been sent to you!");
			setError("");
		}
			
	} catch (e) {
		setError(e.message)
		setSucces("");
	}
};


}