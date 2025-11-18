export default function HiddenChatBox({ unreadMessagesCount, setShowChatBox }) {
	return (
		<div
			style={{
				position: "fixed",
				right: "5rem",
				bottom: 0,
				width: "20rem",
				height: "4.5rem",
				display: "flex",
				alignItems: "center",
				justifyContent: "space-between",
				backgroundColor: "#f9fafb",
				border: "1px solid #e5e7eb",
				borderTopLeftRadius: "10px",
				borderTopRightRadius: "10px",
				boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
				padding: "0 1rem",
			}}
		>
			<h4
				style={{
					margin: 0,
					fontSize: "1rem",
					fontWeight: 600,
					color: "#0f172a",
					display: "flex",
					alignItems: "center",
					gap: "0.5rem",
				}}
			>
				Unread messages:
				<span
					style={{
						backgroundColor: unreadMessagesCount === 0 ? "#e5e7eb" : "#ef4444",
						color: unreadMessagesCount === 0 ? "#2e343dff" : "white",
						padding: "4px 10px",
						borderRadius: "50%",
						fontWeight: 700,
						fontSize: "1rem",
						minWidth: "0.8rem",
						textAlign: "center",
						lineHeight: 1.2,
					}}
				>
					{unreadMessagesCount}
				</span>
			</h4>

			<span
				onClick={() => setShowChatBox(true)}
				style={{
					cursor: "pointer",
					fontSize: "1.6rem",
					userSelect: "none",
					fontWeight: "bold",
					backgroundColor: "#e2e8f0",
					color: "#0f172a",
					display: "inline-flex",
					alignItems: "center",
					justifyContent: "center",
					width: "40px",
					height: "40px",
					borderRadius: "50%",
					boxShadow: "0 2px 6px rgba(0,0,0,0.15)",
					transition: "transform 0.15s ease, box-shadow 0.15s ease",
				}}
				onMouseEnter={(e) => {
					e.currentTarget.style.transform = "scale(1.05)";
					e.currentTarget.style.boxShadow = "0 4px 10px rgba(0,0,0,0.2)";
				}}
				onMouseLeave={(e) => {
					e.currentTarget.style.transform = "scale(1)";
					e.currentTarget.style.boxShadow = "0 2px 6px rgba(0,0,0,0.15)";
				}}
			>
				<p style={{ transform: "translateY(5%)" }}>^</p>
			</span>
		</div>
	);
}
