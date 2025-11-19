export default function InputField({
  messageInput,
  setMessageInput,
  handleSendMessage,
  selectedChatRoom,
  username,
  chatSystem,
  setMessagesByRoom,
	students
}) {
  return (
    <div
      style={{
        position: "absolute",
        bottom: 0,
        right: 0,
        width: "60%",
        height: "4rem",
        borderTop: "2px solid #e5e7eb",
        backgroundColor: "white",
        display: "flex",
        alignItems: "center",
        padding: "0 1rem",
        gap: "1rem",
      }}
    >
      <input
        type="text"
        value={messageInput}
        onChange={(e) => setMessageInput(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === "Enter" && messageInput.trim()) {
            handleSendMessage(
              messageInput,
              selectedChatRoom,
              username,
              setMessageInput,
              chatSystem,
              setMessagesByRoom,
							students
            );
          }
        }}
        placeholder="Skriv en besked..."
        style={{
          flex: 1,
          height: "2.5rem",
          padding: "0 1rem",
          fontSize: "15px",
          border: "1px solid #e5e7eb",
          borderRadius: "20px",
          outline: "none",
          backgroundColor: "#f9fafb",
          transition: "border-color 0.15s ease",
        }}
        onFocus={(e) => {
          e.currentTarget.style.borderColor = "#3b82f6";
          e.currentTarget.style.backgroundColor = "white";
        }}
        onBlur={(e) => {
          e.currentTarget.style.borderColor = "#e5e7eb";
          e.currentTarget.style.backgroundColor = "#f9fafb";
        }}
      />
      <button
        onClick={() =>
          handleSendMessage(
            messageInput,
            selectedChatRoom,
            username,
            setMessageInput,
            chatSystem,
            setMessagesByRoom,
          )
        }
        style={{
          backgroundColor: "#3b82f6",
          color: "white",
          border: "none",
          borderRadius: "20px",
          padding: "0.5rem 1.5rem",
          fontSize: "15px",
          fontWeight: 600,
          cursor: "pointer",
          transition: "background-color 0.15s ease, transform 0.15s ease",
          boxShadow: "0 2px 6px rgba(59, 130, 246, 0.3)",
        }}
        onMouseEnter={(e) => {
          e.currentTarget.style.backgroundColor = "#2563eb";
          e.currentTarget.style.transform = "scale(1.02)";
        }}
        onMouseLeave={(e) => {
          e.currentTarget.style.backgroundColor = "#3b82f6";
          e.currentTarget.style.transform = "scale(1)";
        }}
      >
        Send
      </button>
    </div>
  );
}
