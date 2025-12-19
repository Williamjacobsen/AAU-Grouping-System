import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthProvider";
import "./profileSystem.css";

export default function Profile() {

	const navigate = useNavigate();

	const [newEmail, setNewEmail] = useState("");
	const [newPassword, setNewPassword] = useState("");
	const [newName, setNewName] = useState("");
	const [error, setError] = useState("");
	const [success, setSuccess] = useState("");
	const { user, isLoading: isLoadingUser, setUser } = useAuth();

	useEffect(() => {
		if (error) {
			const timer = setTimeout(() => setError(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [error]);

	useEffect(() => {
		if (success) {
			const timer = setTimeout(() => setSuccess(""), 5000);
			return () => clearTimeout(timer);
		}
	}, [success]);

	if (isLoadingUser) return <>Checking authentication...</>;
	if (!user) return navigate("/sign-in");

	const handleEmailChange = async () => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/coordinator/modifyEmail`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ newEmail }),
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				setError(errorMessage);
				setSuccess("");
				return Promise.resolve();
			}

			navigate("/profile");
			setSuccess("Email updated successfully");
			setError("");
			setUser(prev => ({ ...prev, email: newEmail }));

		} catch (e) {
			setError(e.message);
		}
	};

	const handlePasswordChange = async () => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/coordinator/modifyPassword`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ newPassword }),
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				setError(errorMessage);
				setSuccess("");
				return Promise.resolve();
			}

			navigate("/profile");
			setSuccess("Password updated successfully");
			setError("");
			setUser(prev => ({ ...prev, password: newPassword }));

		} catch (e) {
			setError(e.message);
		}
	};

	const handleNameChange = async () => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/coordinator/modifyName`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify({ newName }),
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				setError(errorMessage);
				setSuccess("");
				return Promise.resolve();
			}

			navigate("/profile");
			setSuccess("Name updated successfully");
			setError("");
			setUser(prev => ({ ...prev, name: newName }));

		} catch (e) {
			setError(e.message);
		}
	};

	const handleLogout = async () => {
		try {
			const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/auth/signOut`, {
				method: "POST",
				credentials: "include"
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				setError(errorMessage);
				return Promise.resolve();
			}


			navigate("/sign-in");
			window.location.reload(); // To refresh the header
		} catch (e) {
			setError(e.message);
		}
	};

	return (
		<div className="container">
			<div className="header-text">Profile</div>

			{error && (<div className="error-box">{error}</div>)}
			{success && (<div className="success-box">{success}</div>)}

			<button className="sign-out" onClick={handleLogout}>
				Log Out
			</button>

			<div className="text">
				<p><b>Name:</b> {user.name}</p>
				<p><b>Email:</b> {user.email}</p>
				<p><b>Role:</b> {user.role}</p>
			</div>

			<hr />
			{user.role !== "Coordinator" &&
				<>
					Since you are not a coordinator, you are not yourself allowed to change your name, email, and password.
					If you want to change any of these, ask your coordinator to change them for you.
				</>
			}
			{user.role === "Coordinator" &&
				<>
					<div className="text">
						<label className="label">
							Change Name
							<input
								type="text"
								placeholder="New name"
								onChange={(e) => setNewName(e.target.value)}
								onKeyDown={(e) => {
									if (e.key === "Enter") {
										handleNameChange();
									}
								}}
							/>
						</label>
						<button className="sign-in" onClick={handleNameChange}>
							Update Name
						</button>
					</div>

					<div className="text">
						<label className="label">
							Change Email
							<input
								type="email"
								placeholder="New email"
								onChange={(e) => setNewEmail(e.target.value)}
								onKeyDown={(e) => {
									if (e.key === "Enter") {
										handleEmailChange();
									}
								}}
							/>
						</label>
						<button className="sign-in" onClick={handleEmailChange}>
							Update Email
						</button>
					</div>

					< div className="text">
						<label className="label">
							Change Password
							<input
								type="password"
								placeholder="New password"
								onChange={(e) => setNewPassword(e.target.value)}
								onKeyDown={(e) => {
									if (e.key === "Enter") {
										handlePasswordChange();
									}
								}}
							/>
						</label>
						<button className="sign-in" onClick={handlePasswordChange}>
							Update Password
						</button>
					</div>
				</>
			}
			<hr />
		</div>
	);
}




