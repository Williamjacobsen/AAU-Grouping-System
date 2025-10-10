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

  const messaging = useRef(null);
  const username = "My username";
  const [target, setTarget] = useState(username);
  const { groups, students } = useAppState();

  useEffect(() => {
    console.log(groups);
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
  });

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
            <div style={{ backgroundColor: "#e2e8f0", paddingBottom: "2rem" }}>
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
                  left: "3rem",
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
                  v
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
              }}
            >
              {/* Sidebar */}
              <div
                style={{
                  backgroundColor: "white",
                  position: "relative",
                  width: "40%",
                  height: "100%",
                  borderRight: "2px solid #e5e7eb",
                }}
              >
                {/* Each name box */}
                <div
                  style={{
                    backgroundColor: "#f1f5f9",
                    position: "relative",
                    width: "100%",
                    minHeight: "5rem",
                    marginBottom: "0.5rem",
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    borderBottom: "1px solid #e5e7eb",
                  }}
                >
                  <h4
                    style={{
                      margin: 0,
                      fontSize: "20px",
                      fontWeight: 600,
                      transform: "translateY(-10%) translateX(-5%)",
                    }}
                  >
                    Some kinda name
                  </h4>
                </div>
              </div>
              {/* Chat area */}
              <div></div>
            </div>
          </div>
        </>
      ) : (
        <div
          style={{
            border: "solid black 3px",
            borderTopLeftRadius: "10px",
            borderTopRightRadius: "10px",
            position: "fixed",
            right: "5rem",
            bottom: 0,
            width: "20rem",
            height: "4rem",
            display: "flex",
          }}
        >
          <h4 style={{ paddingLeft: "1rem" }}>
            Ulæste beskeder:{" "}
            <span
              style={{
                backgroundColor: unreadMessagesCount === 0 ? "" : "red",
                paddingLeft: "10px",
                paddingRight: "10px",
                paddingTop: "5px",
                paddingBottom: "5px",
                borderRadius: "100%",
                fontWeight: "bold",
                fontSize: "large",
              }}
            >
              {unreadMessagesCount}
            </span>
          </h4>
          <span
            onClick={() => setShowChatBox(true)}
            style={{
              cursor: "pointer",
              fontSize: "2rem",
              userSelect: "none",
              fontWeight: "bold",
              backgroundColor: "lightgray",
              position: "relative",
              left: "3rem",
              paddingLeft: "10px",
              paddingRight: "10px",
              marginTop: "15px",
              marginBottom: "10px",
              borderRadius: "100%",
            }}
          >
            ^
          </span>
        </div>
      )}
    </>
  );
}
