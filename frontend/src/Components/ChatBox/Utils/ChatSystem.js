import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

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

export default ChatSystem;
