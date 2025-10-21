package com.aau.grouping_system.ChatSystem.Messages;

import java.security.Principal;
import java.util.Deque;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService;

@Service
public class MessagesService {
	private final WebSocketService webSocketService;

	public MessagesService(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	public Deque<WebSocketService.MessageDatabaseFormat> getAllGroupMessage(String groupId) {
		return webSocketService.groupMessages.get(groupId);
	}

	public Deque<WebSocketService.MessageDatabaseFormat> getAllPrivateMessages(
			String user1,
			String user2) {
		String conversationKey = WebSocketService.getConversationKey(user1, user2);
		return webSocketService.privateMessages.get(conversationKey);
	}

	// TODO: move getUnreadCount
	public int getGroupUnreadCount(String groupId, String username) {
		return webSocketService.getUnreadCount(groupId, username, true);
	}

	public int getPrivateUnreadCount(String user1, String user2, String username) {
		String conversationKey = WebSocketService.getConversationKey(user1, user2);
		return webSocketService.getUnreadCount(conversationKey, username, false);
	}
}
