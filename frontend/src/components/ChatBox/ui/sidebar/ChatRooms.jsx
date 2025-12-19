import getConversationKey from "../../utils/getConversationKey";

export default function chatRooms({
	filteredChatRooms,
	setSelectedChatRoom,
	selectedChatRoom,
	projectSet,
	groupSet,
	studentSet,
	unreadMessagesByRoom,
	setUnreadMessagesByRoom,
	username,
	setUnreadMessagesCount,
}) {
	const handleRoomClick = (chatRoom) => {
		setSelectedChatRoom(chatRoom);

		const storageKey =
			studentSet.has(chatRoom) && username
				? getConversationKey(username, chatRoom)
				: chatRoom;

		setUnreadMessagesCount((prev) => prev - unreadMessagesByRoom[storageKey]);
		setUnreadMessagesByRoom((prev) => ({ ...prev, [storageKey]: 0 }));
	};

	const getUnreadCount = (chatRoom) => {
		const storageKey =
			studentSet.has(chatRoom) && username
				? getConversationKey(username, chatRoom)
				: chatRoom;
		return Number(unreadMessagesByRoom[storageKey]) || 0;
	};

	return (
		<>
			{filteredChatRooms.map((chatRoom) => {
				const unreadCount = getUnreadCount(chatRoom);

				return (
					<div
						key={chatRoom}
						onClick={() => handleRoomClick(chatRoom)}
						style={{
							backgroundColor:
								selectedChatRoom === chatRoom ? "#dbeafe" : "#f1f5f9",
							position: "relative",
							width: "100%",
							minHeight: "5rem",
							marginBottom: "0.5rem",
							display: "flex",
							gap: "1rem",
							alignItems: "center",
							borderBottom: "1px solid #e5e7eb",
							transition: "background-color 0.15s ease",
							cursor: "pointer",
						}}
						onMouseEnter={(e) => {
							if (selectedChatRoom !== chatRoom) {
								e.currentTarget.style.backgroundColor = "#e2e8f0";
							}
						}}
						onMouseLeave={(e) => {
							if (selectedChatRoom !== chatRoom) {
								e.currentTarget.style.backgroundColor = "#f1f5f9";
							}
						}}
					>
						<p style={{ paddingLeft: "2rem" }}>
							{chatRoom === "General"
								? "General:"
								: projectSet.has(chatRoom)
									? "project:"
									: groupSet.has(chatRoom)
										? "group:"
										: studentSet.has(chatRoom)
											? "student:"
											: ""}
						</p>
						<h4
							style={{
								margin: 0,
								fontSize: unreadCount > 0 ? "20.5px" : "20px",
								fontWeight: unreadCount > 0 ? 600 : 500,
								width: "11rem",
								overflow: "hidden",
								textOverflow: "ellipsis",
								whiteSpace: "nowrap",
							}}
						>
							{chatRoom}
						</h4>
						<span
							style={{
								cursor: "pointer",
								userSelect: "none",
								fontWeight: "bold",
								fontSize: "1.5rem",
								display: "inline-flex",
								alignItems: "center",
								justifyContent: "center",
								width: "32px",
								height: "32px",
								borderRadius: "50%",
								backgroundColor: "white",
								boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
								transition: "transform 0.15s ease, box-shadow 0.15s ease",
								position: "absolute",
								right: "2.5rem",
							}}
							onMouseEnter={(e) => {
								e.currentTarget.style.transform = "scale(1.05)";
								e.currentTarget.style.boxShadow = "0 4px 10px rgba(0,0,0,0.15)";
							}}
							onMouseLeave={(e) => {
								e.currentTarget.style.transform = "scale(1)";
								e.currentTarget.style.boxShadow = "0 2px 6px rgba(0,0,0,0.1)";
							}}
						>
							<p style={{ transform: "translateY(-5%) translateX(5%)" }}>
								&gt; {/* This symbol: > */}
							</p>
						</span>
					</div>
				);
			})}
		</>
	);
}
