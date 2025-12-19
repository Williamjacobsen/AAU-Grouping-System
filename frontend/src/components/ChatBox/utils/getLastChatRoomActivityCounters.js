export default async function getUnreadMessagesCounters(username, setChatRoomLastActivity) {
	await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/user/${username}/messages/all/most-recent-timestamps`)
		.then((response) => response.json())
		.then((json) => {
			console.log(json)
			setChatRoomLastActivity(json ?? {});
		})
		.catch((error) => console.error(error));
}