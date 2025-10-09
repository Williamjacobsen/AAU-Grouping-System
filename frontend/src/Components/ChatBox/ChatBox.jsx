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
    <div>
      <h1>WebSocket Test</h1>
    </div>
  );
}
