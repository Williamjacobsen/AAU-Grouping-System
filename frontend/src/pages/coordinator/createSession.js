// This is an example. We will likely delete this at some point.

export default function createSession(sessionName) {
	fetch(
		`http://localhost:8080/coordinator/createSession?sessionName=${sessionName}`,
		{ method: "POST" }
	)
		.then((res) => console.log(res.status))
		.catch((err) => console.error(err));
}
