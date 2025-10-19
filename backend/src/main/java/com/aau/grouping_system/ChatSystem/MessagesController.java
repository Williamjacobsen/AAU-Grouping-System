package com.aau.grouping_system.ChatSystem;

import java.util.Deque;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {
	private final WebSocketController webSocketController;

	public MessagesController(WebSocketController webSocketController) {
		this.webSocketController = webSocketController;
	}

	@GetMapping("/group/{groupId}/messages/get/all")
	public Deque<WebSocketController.MessageDatabaseFormat> getGroupMessage(@PathVariable String groupId) {
		return webSocketController.groupMessages.get(groupId);
	}

	@GetMapping("/private/{user1}/{user2}/messages/get/all")
	public Deque<WebSocketController.MessageDatabaseFormat> getPrivateMessages(
			@PathVariable String user1, 
			@PathVariable String user2) {
		String conversationKey = WebSocketController.getConversationKey(user1, user2);
		return webSocketController.privateMessages.get(conversationKey);
	}
}
