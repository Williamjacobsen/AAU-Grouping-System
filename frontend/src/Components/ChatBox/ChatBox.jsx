import { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

export default function ChatBox() {
  const stompClient = useRef(null);
	const [username] = useState("user" + Math.floor(Math.random() * 1000));

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient.current = Stomp.over(socket);

    stompClient.current.connect(
      { username: username },
      () => {
        console.log("Connected to WebSocket");

        stompClient.current.subscribe("/topic/messages", (message) => {
          console.log("Received:", JSON.parse(message.body));
        });

				stompClient.current.subscribe("/user/queue/reply", (message) => {
          console.log("Private message:", JSON.parse(message.body));
        });

        stompClient.current.send(
          "/app/chat",
          {},
          JSON.stringify({
            content: "Hello from React",
            sender: username,
          })
        );

				setTimeout(() => {
          console.log("Sending private message...");
          stompClient.current.send(
            "/app/private",
            {},
            JSON.stringify({
              content: "This is a private message",
              sender: username,
            })
          );
        }, 2000);
      },
      (error) => {
        console.error("Connection error:", error);
      }
    );

    return () => {
      if (stompClient.current) {
        stompClient.current.disconnect();
        console.log("Disconnected");
      }
    };
  }, [username]);

  return (
    <div>
      <h1>WebSocket Test</h1>
      <p>Check the console for messages</p>
    </div>
  );
}
