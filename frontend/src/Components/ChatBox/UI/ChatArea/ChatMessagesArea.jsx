export default function ChatMessagesArea({ roomMessages, username }) {
  return (
    <div
      style={{
        position: "relative",
        width: "calc(100% - 1.5*2rem)",
        height: "calc(100% - 12rem)",
        overflowY: "auto",
        padding: "1.5rem",
        backgroundColor: "#f9fafb",
      }}
    >
      {roomMessages.map((message) => (
        <div
          key={message.id}
          style={{
            position: "relative",
            width: "100%",
            marginBottom: "1rem",
            display: "flex",
            justifyContent:
              message.sender === username ? "flex-end" : "flex-start",
          }}
        >
          <div
            style={{
              backgroundColor:
                message.sender === username ? "#3b82f6" : "white",
              color: message.sender === username ? "white" : "#0f172a",
              padding: "0.75rem 1rem",
              borderRadius: "10px",
              maxWidth: "70%",
              boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
              position: "relative",
            }}
          >
            <p
              style={{
                margin: 0,
                fontSize: "14px",
                fontWeight: 600,
                marginBottom: "0.25rem",
                opacity: 0.8,
              }}
            >
              {message.sender}
            </p>
            <p
              style={{
                margin: 0,
                fontSize: "15px",
                lineHeight: 1.5,
              }}
            >
              {message.content}
            </p>
            <p
              style={{
                margin: 0,
                fontSize: "12px",
                marginTop: "0.5rem",
                opacity: 0.7,
                textAlign: "right",
              }}
            >
              {message.time}
            </p>
          </div>
        </div>
      ))}
    </div>
  );
}
