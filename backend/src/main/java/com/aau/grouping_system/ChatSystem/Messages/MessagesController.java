package com.aau.grouping_system.ChatSystem.Messages;

import java.util.Deque;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService;

@RestController
public class MessagesController {
	private final MessagesService messagesService;

	public MessagesController(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	@GetMapping("/group/{groupId}/messages/get/all")
	public Deque<WebSocketService.MessageDatabaseFormat> getGroupMessage(@PathVariable String groupId) {
		return messagesService.getAllGroupMessage(groupId);
	}

	@GetMapping("/private/{user1}/{user2}/messages/get/all")
	public Deque<WebSocketService.MessageDatabaseFormat> getPrivateMessages(
			@PathVariable String user1, 
			@PathVariable String user2) {
		return messagesService.getAllPrivateMessages(user1, user2);
	}
}
