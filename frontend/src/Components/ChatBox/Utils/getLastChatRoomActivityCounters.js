export default async function getUnreadMessagesCounters(username, setChatRoomLastActivity) {
	await fetch(`http://localhost:8080/user/${username}/messages/all/most-recent-timestamps`)
		.then((response) => response.json())
		.then((json) => {
			console.log(json)
			setChatRoomLastActivity(json ?? {});
		})
		.catch((error) => console.error(error));
}