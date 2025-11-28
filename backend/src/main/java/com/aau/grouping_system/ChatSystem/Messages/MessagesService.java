package com.aau.grouping_system.ChatSystem.Messages;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Service;

import com.aau.grouping_system.ChatSystem.ChatRoom;
import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService;
import com.aau.grouping_system.Database.Database;

@Service
public class MessagesService {

	private final Database db;

	public MessagesService(Database db) {
		this.db = db;
	}

	private ChatRoom getChatRoom(String conversationKey) {
		String chatRoomId = db.getChatRoomKeyIndex().get(conversationKey);

		if (chatRoomId == null) {
			return null;
		}

		return db.getChatRooms().getItem(chatRoomId);
	}

	public Deque<WebSocketService.MessageDatabaseFormat> getAllGroupMessage(String groupId) {
		ChatRoom room = getChatRoom(groupId);

		return (room != null) ? room.getMessages() : new ConcurrentLinkedDeque<>();
	}

	public Deque<WebSocketService.MessageDatabaseFormat> getAllPrivateMessages(
			String user1,
			String user2) {

		String conversationKey = WebSocketService.getConversationKey(user1, user2);
		ChatRoom room = getChatRoom(conversationKey);

		return (room != null) ? room.getMessages() : new ConcurrentLinkedDeque<>();
	}

	public int getUnreadCount(String conversationKey, String username) {
		ChatRoom chatRoom = getChatRoom(conversationKey);

		if (chatRoom == null || chatRoom.getMessages().isEmpty())
			return 0;

		int maxId = chatRoom.getMessages().peekLast().id();
		int lastRead = chatRoom.getLastReadMap().getOrDefault(username, -1);

		return Math.max(0, (maxId - lastRead));
	}

	public ConcurrentHashMap<String, Integer> getAllUnreadMessages(String username) {
		ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();

		for (ChatRoom room : db.getChatRooms().getAllItems().values()) {
			String key = room.getConversationKey();

			if (room.isGroup()) {
				result.put(key, getUnreadCount(key, username));
			} else {
				if (key.contains(username)) {
					result.put(key, getUnreadCount(key, username));
				}
			}
		}

		return result;
	}

	public ConcurrentHashMap<String, Long> getChatRoomLastActivityTimestamps(String username) {
		ConcurrentHashMap<String, Long> result = new ConcurrentHashMap<>();

		for (ChatRoom room : db.getChatRooms().getAllItems().values()) {
			if (room.getMessages().isEmpty())
				continue;

			String key = room.getConversationKey();
			long timestamp = room.getMessages().peekLast().timestamp();

			if (room.isGroup()) {
				result.put(key, timestamp);
			} else {
				if (key.contains(username)) {
					result.put(key, timestamp);
				}
			}
		}

		return result;
	}
}