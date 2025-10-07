package com.aau.grouping_system.ChatSystem;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	public record Message(String content, String sender) {
	}

	// Receives messages from /app/chat (client sends to /app/chat)
	// Broadcasts response to all subscribers of /topic/messages
	@MessageMapping("/chat")
	@SendTo("/topic/messages")
	public Message sendMessage(Message message) {
		System.out.println("Received message on /app/chat");
		return new Message(message.content, message.sender);
	}

	// Send to specific user
	@MessageMapping("/private")
	@SendToUser("/queue/reply")
	public Message sendPrivateMessage(Message message, Principal principal) {
		System.out.println("Received private message from: " +
				(principal != null ? principal.getName() : "NULL"));
		return message;
	}
}