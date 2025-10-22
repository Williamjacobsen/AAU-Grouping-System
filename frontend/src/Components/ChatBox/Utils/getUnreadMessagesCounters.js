export default async function getUnreadMessagesCounters(username, setUnreadMessagesByRoom, setUnreadMessagesCount) {
	await fetch(`http://localhost:8080/user/${username}/messages/unread/get/all`)
		.then((response) => response.json())
		.then((json) => {
			setUnreadMessagesByRoom(json ?? {});
			setUnreadMessagesCount(Object.values(json).reduce((a, n) => a + n, 0));
		})
		.catch((error) => console.error(error));
	}