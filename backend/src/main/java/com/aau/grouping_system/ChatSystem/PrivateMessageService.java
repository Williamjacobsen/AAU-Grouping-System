package com.aau.grouping_system.ChatSystem;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PrivateMessageService {

	private final SimpMessagingTemplate messagingTemplate;

	public PrivateMessageService(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public void sendPrivateMessage(String username, String message) {
		// Server sends the message to /user/{username}/queue/messages
		messagingTemplate.convertAndSendToUser(username, "/queue/messages", message);
	}
}
