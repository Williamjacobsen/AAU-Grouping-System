package com.aau.grouping_system.ChatSystem.Messages;

import java.util.Deque;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService;
import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService.MessageDatabaseFormat;

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

	public int getPrivateUnreadCount(String conversationKey, String username) {
		return webSocketService.getUnreadCount(conversationKey, username, false);
	}

	public ConcurrentHashMap<String, Integer> getAllUnreadMessages(String username) {
		ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();

		for (Entry<String, Deque<MessageDatabaseFormat>> groupEntry : webSocketService.groupMessages.entrySet()) {
			String key = groupEntry.getKey();

			result.put(key, getGroupUnreadCount(key, username));
		}	

		for (Entry<String, Deque<MessageDatabaseFormat>> groupEntry : webSocketService.privateMessages.entrySet()) {
			String key = groupEntry.getKey();

			result.put(key, getPrivateUnreadCount(key, username));
		}	

		System.out.println(result.toString());

		return result;
	}
}
