export default function ChatRoomHeader({ selectedChatRoom }) {
  return (
    <div
      style={{
        backgroundColor: "#f1f5f9",
        position: "relative",
        width: "100%",
        height: "4rem",
        borderBottom: "2px solid #e5e7eb",
        display: "flex",
        alignItems: "center",
        paddingLeft: "2rem",
      }}
    >
      <h4
        style={{
          fontSize: "18px",
          fontWeight: 600,
          color: "#0f172a",
        }}
      >
        {selectedChatRoom}
      </h4>
    </div>
  );
}
