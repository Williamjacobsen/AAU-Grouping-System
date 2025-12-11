import React from "react";
import "./About.css";

export default function About() {
	return (
		<div className="about-container">
			<h1 className="about-title">Streamlining Group Formation</h1>

			<p className="about-description">
				This platform simplifies the group formation process for students, coordinators, and supervisors.
			</p>

			<h2 className="section-heading">Usage Tutorial:</h2>

			<p className="tutorial-intro">
				Below is a quick overview of how each role uses the system.
				<br />
				For navigation tips, see the options in the header.
			</p>

			<div className="tutorial-row">
				<div className="user-card">
					<h3>Students</h3>
					<ol>
						<li>Fill out your wish form.</li>
						<li>Explore available projects.</li>
						<li>View students and send join requests.</li>
						<li>Manage or create your group.</li>
					</ol>
				</div>

				<div className="user-card">
					<h3>Coordinators</h3>
					<ol>
						<li>Set up the session.</li>
						<li>Send login codes.</li>
						<li>Configure supervisors and projects.</li>
						<li>Finalize the groups.</li>
					</ol>
				</div>

				<div className="user-card">
					<h3>Supervisors</h3>
					<ol>
						<li>Add project proposals.</li>
						<li>Manage group supervision load.</li>
					</ol>
				</div>
			</div>
		</div>
	);
}
