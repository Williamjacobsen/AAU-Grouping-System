export default function Header({ setShowChatBox }) {
  return (
    <div
      style={{
        backgroundColor: "#dae4f1ff",
        paddingBottom: "2rem",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        paddingLeft: "2rem",
        paddingRight: "2.5rem",
      }}
    >
      <h4
        style={{
          margin: 0,
          padding: 0,
          fontWeight: 600,
          fontSize: "18px",
          position: "relative",
          top: "1rem",
        }}
      >
        Chat Messages
      </h4>
      <span
        onClick={() => setShowChatBox(false)}
        style={{
          cursor: "pointer",
          userSelect: "none",
          fontWeight: "bold",
          fontSize: "2rem",
          display: "inline-flex",
          alignItems: "center",
          justifyContent: "center",
          width: "40px",
          height: "40px",
          borderRadius: "50%",
          backgroundColor: "white",
          boxSizing: "border-box",
          padding: 0,
          lineHeight: 1,
          transform: "translateY(-1px)",
          position: "relative",
          top: "1rem",
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
        <p
          style={{
            position: "relative",
            bottom: "4px",
            fontWeight: 400,
          }}
        >
          x
        </p>
      </span>
    </div>
  );
}
