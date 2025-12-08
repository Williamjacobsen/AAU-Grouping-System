package com.aau.grouping_system.ChatSystem.WebSocket;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.aau.grouping_system.ChatSystem.ChatRoom;
import com.aau.grouping_system.Database.Database;

@Service
public class WebSocketService {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public record IncommingMessage(String content, String sender, String target, String clientMessageId) {
	}

	public record MessageDatabaseFormat(Integer id, String content, String sender, String target, String time,
			long timestamp) {
	};

	private final SimpMessagingTemplate ws;
	private final Database db;

	public WebSocketService(SimpMessagingTemplate ws, Database db) {
		this.ws = ws;
		this.db = db;
	}

	private ChatRoom getChatRoom(String conversationKey) {
		String chatRoomId = db.getChatRoomKeyIndex().get(conversationKey);

		if (chatRoomId == null) {
			return null;
		}

		return db.getChatRooms().getItem(chatRoomId);
	}

	public ChatRoom getOrCreateChatRoom(String conversationKey, boolean isGroup) {
		ChatRoom chatRoom = getChatRoom(conversationKey);

		if (chatRoom == null) {
			ChatRoom newRoom = new ChatRoom(conversationKey, isGroup);

			ChatRoom indexedRoom = db.getChatRooms().addItem(null, newRoom);

			db.getChatRoomKeyIndex().put(conversationKey, indexedRoom.getId());

			return indexedRoom;
		}

		return chatRoom;
	}

	@SuppressWarnings("null")
	public void sendGroupMessage(String groupId, IncommingMessage message, Principal principal) {
		MessageDatabaseFormat formattedMessage = addToDatabase(groupId, message, true);

		ws.convertAndSend("/group/" + groupId + "/messages", formattedMessage);

		sendAckToUser(principal, groupId, null, message.clientMessageId());
	}

	@SuppressWarnings("null")
	public void sendPrivateMessage(IncommingMessage message, Principal principal) {
		String key = getConversationKey(message.sender(), message.target());

		MessageDatabaseFormat formattedMessage = addToDatabase(key, message, false);

		ws.convertAndSendToUser(
				message.target(),
				"/private/reply",
				formattedMessage);

		ws.convertAndSendToUser(
				message.sender(),
				"/private/reply",
				formattedMessage);

		sendAckToUser(principal, null, message.target(), message.clientMessageId());
	}

	@SuppressWarnings("null")
	private MessageDatabaseFormat addToDatabase(
			String key,
			IncommingMessage message,
			boolean isGroup) {

		ChatRoom chatRoom = getOrCreateChatRoom(key, isGroup);

		int id = chatRoom.getAndIncrementNextId();

		MessageDatabaseFormat formattedMessage = new MessageDatabaseFormat(
				id,
				message.content(),
				message.sender(),
				message.target(),
				LocalDateTime.now().format(FORMATTER),
				System.currentTimeMillis());

		chatRoom.getMessages().add(formattedMessage);

		chatRoom.getLastReadMap().merge(message.sender(), id, Math::max);

		return formattedMessage;
	}

	@SuppressWarnings("null")
	public void markReadUpTo(String conversationKey, String username, int upToMessageId, boolean isGroup) {
		if (username == null || username.isEmpty())
			return;

		ChatRoom chatRoom = getChatRoom(conversationKey);
		if (chatRoom == null)
			return;

		var lastReadForConversation = chatRoom.getLastReadMap();
		lastReadForConversation.merge(username, upToMessageId, Math::max);
	}

	public int getUnreadCount(String conversationKey, String username, boolean isGroup) {
		ChatRoom chatRoom = getChatRoom(conversationKey);

		if (chatRoom == null || chatRoom.getMessages().isEmpty())
			return 0;

		int maxId = chatRoom.getMessages().peekLast().id();
		var lastReadMap = chatRoom.getLastReadMap();
		int lastRead = lastReadMap.getOrDefault(username, -1);

		return Math.max(0, (maxId - lastRead));
	}

	public static String getConversationKey(String user1, String user2) {
		if (user1.compareTo(user2) < 0) {
			return user1 + "-" + user2;
		} else {
			return user2 + "-" + user1;
		}
	}

	@SuppressWarnings("null")
	private void sendAckToUser(Principal principal,
			String groupId,
			String target,
			String messageId) {

		if (principal == null)
			return;

		Map<String, Object> ack = new HashMap<>();
		ack.put("type", "SENT");
		if (groupId != null && !groupId.isEmpty())
			ack.put("groupId", groupId);
		if (target != null && !target.isEmpty())
			ack.put("target", target);
		if (messageId != null && !messageId.isEmpty())
			ack.put("messageId", messageId);
		ack.put("timestamp", System.currentTimeMillis());

		ws.convertAndSendToUser(principal.getName(), "/queue/acks", ack);
	}
}