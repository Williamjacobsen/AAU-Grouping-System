import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default function ChatBox() {
  const [client, setClient] = useState(null);
  const [connected, setConnected] = useState(false);
  const [message, setMessage] = useState("");
  const [receivedMessages, setReceivedMessages] = useState([]);

  useEffect(() => {
    const stompClient = new Client({
      webSocketFactory: () => new SockJS("http://localhost:8080/ws"), 
      reconnectDelay: 5000,
      heartbeatIncoming: 20000, 
      heartbeatOutgoing: 10000,
    });

    stompClient.onConnect = () => {
      setConnected(true);
      stompClient.subscribe("/topic/messages", (msg) => {
        setReceivedMessages((prev) => [...prev, msg.body]);
      });
      // stompClient.subscribe('/user/some-username/queue/messages', (msg) => {
      //   setReceivedMessages((prev) => [...prev, `Private: ${msg.body}`]);
      // });
    };

    stompClient.onStompError = (frame) => {
      console.error("Broker reported error: " + frame.headers["message"]);
      console.error("Additional details: " + frame.body);
    };

    stompClient.activate();
    setClient(stompClient);

    return () => {
      if (stompClient) {
        stompClient.deactivate();
      }
    };
  }, []);

  const sendMessage = () => {
    if (client && connected && message.trim()) {
      client.publish({
        destination: "/app/send",
        body: message,
      });
      setMessage("");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Chat System Test</h2>
      {!connected ? (
        <p>Connecting...</p>
      ) : (
        <>
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Type a message..."
            style={{ width: "300px", marginRight: "10px" }}
          />
          <button onClick={sendMessage}>Send</button>
          <div
            style={{
              marginTop: "20px",
              border: "1px solid #ccc",
              padding: "10px",
              height: "200px",
              overflowY: "auto",
            }}
          >
            <h3>Received Messages:</h3>
            {receivedMessages.map((msg, index) => (
              <p key={index}>{msg}</p>
            ))}
          </div>
        </>
      )}
    </div>
  );
};