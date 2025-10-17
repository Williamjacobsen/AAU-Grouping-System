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
    this.pendingAcks = new Map();
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

        this.subscribe("/user/queue/acks", (ack) => {
          const id = ack?.messageId;
          const resolve = id && this.pendingAcks.get(id);
          if (resolve) {
            resolve();
            this.pendingAcks.delete(id);
            console.log(`Ack - messageId: ${id}`);
          }
        });

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

  send(destination, message, timeoutMs = 5000) {
    if (!this.stompClient?.connected) {
      return Promise.reject(new Error("STOMP not connected"));
    }

    const clientMessageId = `message-${Date.now()}-${Math.random()
      .toString()
      .slice(2)}`;

    return new Promise((resolve, reject) => {
      const timeout = setTimeout(() => {
        this.pendingAcks.delete(clientMessageId);
        reject(new Error("No ack (timeout)"));
      }, timeoutMs);

      this.pendingAcks.set(clientMessageId, () => {
        clearTimeout(timeout);
        resolve();
      });

      this.stompClient.send(
        destination,
        {},
        JSON.stringify({ ...message, clientMessageId })
      );
    });
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
  const { students, chatRooms } = useAppState();

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/group/${selectedChatRoom}/messages/get/all`
        );
        const data = await response.json();
        console.log(data);
      } catch (error) {
        console.error("Error fetching messages:", error);
      }
    };

    if (selectedChatRoom) {
      fetchMessages();
    }
  }, [selectedChatRoom]);

  useEffect(() => {
    console.log(chatRooms);
    console.log(students);

    messaging.current = new ChatSystem("http://localhost:8080/ws", username);

    messaging.current.connect(() => {
      // TODO: subscribe to all chatrooms.

      messaging.current.subscribe("/group/1/messages", (message) => {
        console.log("Received:", message);
      });

      messaging.current.subscribe("/user/private/reply", (message) => {
        console.log("Private message:", message);
      });

      messaging.current.send(`/group/1/send`, {
        content: "Group test message",
        sender: username,
      });

      messaging.current.send("/private/send", {
        content: "This is a private test message",
        sender: username,
        target: username,
      });
    });

    return () => {
      if (messaging.current) {
        messaging.current.disconnect();
      }
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
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
        sender: username,
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
        text: "What 9 + 10?",
        time: "11:15",
      },
      {
        id: 2,
        sender: username,
        text: "21",
        time: "11:20",
      },
    ],
  };

  const handleSendMessage = async () => {
    const content = messageInput.trim();
    if (!content || !selectedChatRoom) return;

    const direct = selectedChatRoom.startsWith("student");
    const destination = direct
      ? "/private/send"
      : `/group/${selectedChatRoom}/send`;
    const payload = direct
      ? { content, sender: username, target: selectedChatRoom }
      : { content, sender: username };

    setMessageInput("");

    try {
      await messaging.current.send(destination, payload);
    } catch (e) {
      console.error("Failed to send:", e);
      // TODO: undo if failed
    }
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
                      {messages[selectedChatRoom]?.map((message) => (
                        <div
                          key={message.id}
                          style={{
                            position: "relative",
                            width: "100%",
                            marginBottom: "1rem",
                            display: "flex",
                            justifyContent:
                              message.sender === username
                                ? "flex-end"
                                : "flex-start",
                          }}
                        >
                          <div
                            style={{
                              backgroundColor:
                                message.sender === username
                                  ? "#3b82f6"
                                  : "white",
                              color:
                                message.sender === username
                                  ? "white"
                                  : "#0f172a",
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
                              {message.text}
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
                    {/* Message Input */}
                    <div
                      style={{
                        position: "absolute",
                        bottom: 20,
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
                            handleSendMessage();
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
                        onClick={() => handleSendMessage()}
                        style={{
                          backgroundColor: "#3b82f6",
                          color: "white",
                          border: "none",
                          borderRadius: "20px",
                          padding: "0.5rem 1.5rem",
                          fontSize: "15px",
                          fontWeight: 600,
                          cursor: "pointer",
                          transition:
                            "background-color 0.15s ease, transform 0.15s ease",
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
                        transform: "translateY(-100%)",
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
