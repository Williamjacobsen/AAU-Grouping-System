package com.aau.grouping_system.ChatSystem;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

public class MessageController {

	@MessageMapping("/send") // Client sends message to backend_url/app/send
	@SendTo("/topic/messages")
	public String processMessage(String message) {
		System.out.println("Received: "+ message);
		return "Received: " + message;
	}

}