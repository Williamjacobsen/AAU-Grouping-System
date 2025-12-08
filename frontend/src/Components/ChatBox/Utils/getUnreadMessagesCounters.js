export default async function getUnreadMessagesCounters(username, setUnreadMessagesByRoom, setUnreadMessagesCount) {
	await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/user/${username}/messages/unread/get/all`)
		.then((response) => response.json())
		.then((json) => {
			console.log(json)
			setUnreadMessagesByRoom(json ?? {});
			setUnreadMessagesCount(Object.values(json).reduce((a, n) => a + n, 0));
		})
		.catch((error) => console.error(error));
}