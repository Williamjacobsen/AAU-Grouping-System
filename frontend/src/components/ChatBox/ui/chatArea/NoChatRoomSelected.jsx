export default function NoChatRoomSelected() {
	return (
		<div
			style={{
				position: "relative",
				width: "100%",
				height: "100%",
				display: "flex",
				alignItems: "center",
				justifyContent: "center",
			}}
		>
			<p
				style={{
					fontSize: "16px",
					color: "#64748b",
					fontWeight: 500,
					transform: "translateY(-100%)",
				}}
			>
				Select a chat to view messages
			</p>
		</div>
	);
}
