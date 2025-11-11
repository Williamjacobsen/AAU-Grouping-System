import React from "react";
import "./About.css";

export default function About() {
	return (
		<div className="about-container">
			<h1 className="about-title">Streamlining Group Formation</h1>
			<p className="about-description">This website aims to make group formation easier to manage for both semester coordinators and students.</p>
			<ol className="features-list">
				<li>
					Feature 1...
				</li>
				<li>
					Feature 2...
				</li>
			</ol>
			<h2 className="section-heading">Usage Tutorial:</h2>
			<div className="user-section">
				<h3>For semester coordinators:</h3>
				<ol className="steps-list">
					<li>
						Step 1...
					</li>
					<li>
						Step 2...
					</li>
				</ol>
			</div>
			<div className="user-section">
				<h3>For supervisors:</h3>
				<ol className="steps-list">
					<li>
						Step 1...
					</li>
					<li>
						Step 2...
					</li>
				</ol>
			</div>
			<div className="user-section">
				<h3>For students:</h3>
				<ol className="steps-list">
					<li>
						Step 1...
					</li>
					<li>
						Step 2...
					</li>
				</ol>
			</div>
			<div className="contact-section">
				<h2 className="section-heading">Contact Info:</h2>
				Contact support using one of the following means:
				<ul className="contact-list">
					<li>
						<span className="contact-label">Support phone number:</span> <span className="contact-value">12 34 56 78</span>
					</li>
					<li>
						<span className="contact-label">Support email:</span> <span className="contact-value">example@email.com</span>
					</li>
				</ul>
			</div>
		</div>
	);
}