import { useState } from "react";

export default function Sidebar({
  chatRooms,
  selectedChatRoom,
  setSelectedChatRoom,
  unreadMessagesByRoom,
  projects,
  groups,
  students,
}) {
  const [query, setQuery] = useState("");

  const projectSet = new Set(projects);
  const groupSet = new Set(groups);
  const studentSet = new Set(students);

  const filteredChatRooms = chatRooms.filter((room) =>
    room.toLowerCase().includes(query.toLowerCase())
  );

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
      {/* Search */}
      <input
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Search…"
        style={{
          padding: "0.7rem 1rem",
          border: "none",
					outline: "none",
					width: "90%",
					borderBottom: "1px solid #e5e7eb",
        }}
      />
      {/* Each chat room / student box */}
      {filteredChatRooms.map((chatRoom) => (
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
              fontSize:
                Number(unreadMessagesByRoom[chatRoom]) > 0 ? "20.5px" : "20px",
              fontWeight:
                Number(unreadMessagesByRoom[chatRoom]) > 0 ? 600 : 500,
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
