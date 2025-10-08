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

	@MessageMapping("/group/send")
	@SendTo("/group/messages")
	public Message sendMessage(Message message) {
		System.out.println("Received message on /group/send");
		return new Message(message.content, message.sender);
	}

	@MessageMapping("/private/send")
	@SendToUser("/private/reply")
	public Message sendPrivateMessage(Message message, Principal principal) {
		System.out.println("Received private message from: " +
				(principal != null ? principal.getName() : "NULL"));
		return message;
	}
}