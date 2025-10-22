export default function sortChatRooms(chatRooms, unreadMessagesByRoom) {
	const getCount = (room) => Number((unreadMessagesByRoom ?? {})[room] ?? 0);

	const rooms = [...chatRooms].sort((a, b) => {
		const ua = getCount(a) > 0 ? 1 : 0;
		const ub = getCount(b) > 0 ? 1 : 0;
		return ub - ua || a.localeCompare(b);
	});

	return rooms;
}