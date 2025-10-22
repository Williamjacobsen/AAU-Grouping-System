export default function Sidebar({
  chatRooms,
  selectedChatRoom,
  setSelectedChatRoom,
	unreadMessagesByRoom,
}) {
  return (
    <div
      style={{
        backgroundColor: "white",
        position: "relative",
        width: "35rem",
        height: "100%",
        borderRight: "2px solid #e5e7eb",
      }}
    >
      {/* Each chat room / student box */}
      {chatRooms.map((chatRoom) => (
        <div
          key={chatRoom}
          onClick={() => setSelectedChatRoom(chatRoom)}
          style={{
            backgroundColor:
              selectedChatRoom === chatRoom ? "#dbeafe" : "#f1f5f9",
            position: "relative",
            width: "100%",
            minHeight: "5rem",
            marginBottom: "0.5rem",
            display: "flex",
            justifyContent: "center",
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
          <h4
            style={{
              margin: 0,
              fontSize: Number(unreadMessagesByRoom[chatRoom]) > 0 ? "20.5px" : "20px",
              fontWeight: Number(unreadMessagesByRoom[chatRoom]) > 0 ? 700 : 600,
              position: "relative",
              right: "2.5rem",
              transform: "translateY(-10%) translateX(0%)",
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
      ))}
    </div>
  );
}
