import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { useAppState } from "../../AppStateContext";

class ChatSystem {
  constructor(url, sender) {
    this.url = url;
    this.sender = sender;
    this.stompClient = null;
    this.subscriptions = [];
  }

  connect(onConnected, onError) {
    this.stompClient = Stomp.over(() => new SockJS(this.url));

    this.stompClient.debug = () => {};
    this.stompClient.heartbeat.outgoing = 20000;
    this.stompClient.heartbeat.incoming = 10000;

    this.stompClient.connect(
      { username: this.sender },
      () => {
        console.log("Connected to WebSocket");
        if (onConnected) onConnected();
      },
      (error) => {
        console.error("Connection error:", error);
        if (onError) onError(error);
      }
    );
  }

  subscribe(destination, callback) {
    if (this.stompClient && this.stompClient.connected) {
      const subscription = this.stompClient.subscribe(
        destination,
        (message) => {
          callback(JSON.parse(message.body));
        }
      );
      this.subscriptions.push(subscription);
      return subscription;
    }
  }

  send(destination, message) {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.send(destination, {}, JSON.stringify(message));
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.subscriptions.forEach((subscription) => subscription.unsubscribe());
      this.subscriptions = [];
      this.stompClient.disconnect();
      console.log("Disconnected");
    }
  }
}

export default function ChatBox() {
  const [showChatBox, setShowChatBox] = useState(false);
  const [unreadMessagesCount, setUnreadMessagesCount] = useState(0);
  const [selectedChatRoom, setSelectedChatRoom] = useState(null);
  const [messageInput, setMessageInput] = useState("");

  const messaging = useRef(null);
  const username = "My username";
  const [target, setTarget] = useState(username);
  const { students, chatRooms } = useAppState();

	// WEBSOCKET DEMO:
  useEffect(() => {
    console.log(chatRooms);
    console.log(students);

    messaging.current = new ChatSystem("http://localhost:8080/ws", username);

    messaging.current.connect(() => {
      messaging.current.subscribe("/group/1/messages", (message) => {
        console.log("Received:", message);
      });

      messaging.current.subscribe("/user/private/reply", (message) => {
        console.log("Private message:", message);
      });

      messaging.current.send(`/group/1/send`, {
        content: "Hello from React",
        sender: username,
      });

      messaging.current.send("/private/send", {
        content: "This is a private message",
        sender: username,
        target: target,
      });
    });

    return () => {
      if (messaging.current) {
        messaging.current.disconnect();
      }
    };
  }, []);

	// DEMO DATA:
  const messages = {
    "student 1": [
      {
        id: 1,
        sender: "student 1",
        text: "Yo my boi",
        time: "10:30",
      },
      {
        id: 2,
        sender: "my names jeff",
        text: "test",
        time: "10:32",
      },
      {
        id: 3,
        sender: "student 1",
        text: "gg",
        time: "10:33",
      },
    ],
    "student 2": [
      {
        id: 1,
        sender: "stundet 2",
        text: "Hvornår skal opgaven afleveres?",
        time: "11:15",
      },
      {
        id: 2,
        sender: "Gerry the G",
        text: "Opgaven skal afleveres på fredag",
        time: "11:20",
      },
    ],
  };

  return (
    <>
      {showChatBox ? (
        <>
          {/* Chat box when open */}
          <div
            style={{
              border: "1px solid #e5e7eb",
              borderTopLeftRadius: "10px",
              borderTopRightRadius: "10px",
              position: "fixed",
              right: "5rem",
              bottom: 0,
              width: "60rem",
              height: "50rem",
              backgroundColor: "#f9fafb",
              boxShadow: "0 4px 16px rgba(0,0,0,0.15)",
              overflow: "hidden",
            }}
          >
            {/* Header */}
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
                  e.currentTarget.style.boxShadow =
                    "0 4px 10px rgba(0,0,0,0.2)";
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = "scale(1)";
                  e.currentTarget.style.boxShadow =
                    "0 2px 6px rgba(0,0,0,0.15)";
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
            {/* Main area */}
            <div
              style={{
                backgroundColor: "#f9fafb",
                width: "100%",
                height: "93%",
                position: "relative",
                display: "flex",
              }}
            >
              {/* Sidebar */}
              <div
                style={{
                  backgroundColor: "white",
                  position: "relative",
                  width: "30vw",
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
                        fontSize: "20px",
                        fontWeight: 600,
                        position: "relative",
                        right: "2.5rem",
                        transform: "translateY(-10%) translateX(-5%)",
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
                        transition:
                          "transform 0.15s ease, box-shadow 0.15s ease",
                        position: "absolute",
                        right: "2.5rem",
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.transform = "scale(1.05)";
                        e.currentTarget.style.boxShadow =
                          "0 4px 10px rgba(0,0,0,0.15)";
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.transform = "scale(1)";
                        e.currentTarget.style.boxShadow =
                          "0 2px 6px rgba(0,0,0,0.1)";
                      }}
                    >
                      <p
                        style={{ transform: "translateY(-5%) translateX(5%)" }}
                      >
                        &gt; {/* This symbol: > */}
                      </p>
                    </span>
                  </div>
                ))}
              </div>
              {/* Chat area */}
              <div
                style={{
                  width: "100%",
                  display: "flex",
                  flexDirection: "column",
                }}
              >
                {selectedChatRoom ? (
                  <>
                    {/* Chat header */}
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
                    {/* Chat Messages */}
                    <div>
                      <p>dawda</p>
                      <p>dawdad</p>
                    </div>
                  </>
                ) : (
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
												transform: "translateY(-100%)"
                      }}
                    >
                      Vælg en chat for at se beskeder
                    </p>
                  </div>
                )}
              </div>
            </div>
          </div>
        </>
      ) : (
        <>
          {/* Hidden/Closed chat box */}
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
              Ulæste beskeder:
              <span
                style={{
                  backgroundColor:
                    unreadMessagesCount === 0 ? "#e5e7eb" : "#ef4444",
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
        </>
      )}
    </>
  );
}
