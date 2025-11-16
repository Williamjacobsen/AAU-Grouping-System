package com.aau.grouping_system.ChatSystem.Messages;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService;
import com.aau.grouping_system.InputValidation.NoDangerousCharacters;

import jakarta.validation.constraints.NotBlank;

@RestController
public class MessagesController {
	private final MessagesService messagesService;

	public MessagesController(MessagesService messagesService) {
		this.messagesService = messagesService;
	}

	@GetMapping("/group/{groupId}/messages/get/all")
	public Deque<WebSocketService.MessageDatabaseFormat> getGroupMessage(
			@NoDangerousCharacters @NotBlank @PathVariable String groupId) {
		return messagesService.getAllGroupMessage(groupId);
	}

	@GetMapping("/private/{user1}/{user2}/messages/get/all")
	public Deque<WebSocketService.MessageDatabaseFormat> getPrivateMessages(
			@NoDangerousCharacters @NotBlank @PathVariable String user1,
			@NoDangerousCharacters @NotBlank @PathVariable String user2) {
		return messagesService.getAllPrivateMessages(user1, user2);
	}

	@GetMapping("/user/{username}/messages/unread/get/all")
	public ConcurrentHashMap<String, Integer> getAllUnreadMessages(
			@NoDangerousCharacters @NotBlank @PathVariable String username) {
		System.out.println(String.format("Received Get: /user/%s/messages/unread/get/all", username));
		return messagesService.getAllUnreadMessages(username);
	}

	@GetMapping("/user/{username}/messages/all/most-recent-timestamps")
	public ConcurrentHashMap<String, Long> getChatRoomLastActivityTimestamps(
			@NoDangerousCharacters @NotBlank @PathVariable String username) {
		System.out.println(String.format("Received Get: /messages/most-recent-timestamp/get/all", username));
		return messagesService.getChatRoomLastActivityTimestamps(username);
	}
}
