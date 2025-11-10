package com.aau.grouping_system.ChatSystem.WebSocket;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public record Message(String content, String sender, String target, String clientMessageId) {
	}

	public record MessageDatabaseFormat(Integer id, String content, String sender, String target, String time,
			long timestamp) {
	};

	public final ConcurrentHashMap<String, Deque<MessageDatabaseFormat>> groupMessages = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<String, Deque<MessageDatabaseFormat>> privateMessages = new ConcurrentHashMap<>();

	/*
	 * Looks like this:
	 * {
	 * "group 1": [
	 * {id: 0, target: null, sender: "student 2", content: "bla bla bla", time:
	 * "2025-10-19 20:20", timestamp: 1739961600000},
	 * {id: 1, target: null, sender: "student 1", content: "bla bla", time:
	 * "2025-10-19 20:25", timestamp: 1739961900000},
	 * ],
	 * "group 2": [
	 * {id: 0, target: null, sender: "student 1", content: "bla bla", time:
	 * "2025-10-19 20:20", timestamp: 1739961600000},
	 * {id: 1, target: null, sender: "student 3", content: "bla bla bla", time:
	 * "2025-10-19 20:25", timestamp: 1739961900000},
	 * {id: 2, target: null, sender: "student 2", content: "bla", time:
	 * "2025-10-19 20:30", timestamp: 1739962200000},
	 * ],
	 * }
	 * 
	 * In Group, to delete, just do groupMessages.remove(groupName)
	 */

	private final SimpMessagingTemplate ws;

	private final ConcurrentHashMap<String, AtomicInteger> groupNextId = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, AtomicInteger> privateNextId = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> groupLastRead = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> privateLastRead = new ConcurrentHashMap<>();

	/**
	 * Last read id (student 1 in group 1, has read the message of id 10 etc)
	 * 
	 * {
	 * "group1": {
	 * "student1": 10,
	 * "student2": 7
	 * },
	 * "group2": {
	 * "student3": 15
	 * }
	 * }
	 */

	public WebSocketService(SimpMessagingTemplate ws) {
		this.ws = ws;
	}

	public void sendGroupMessage(String groupId, Message message, Principal principal) {
		MessageDatabaseFormat formattedMessage = addToDatabase(groupId, message, groupMessages, groupNextId, groupLastRead);

		ws.convertAndSend("/group/" + groupId + "/messages", formattedMessage);

		sendAckToUser(principal, groupId, null, message.clientMessageId());
	}

	public void sendPrivateMessage(Message message, Principal principal) {
		String key = getConversationKey(message.sender(), message.target());

		MessageDatabaseFormat formattedMessage = addToDatabase(key, message, privateMessages, privateNextId,
				privateLastRead);

		ws.convertAndSendToUser(
				message.target(),
				"/private/reply",
				formattedMessage);

		// This is not the best approach, i should add the message for the sender client
		// side, when ack is received.
		ws.convertAndSendToUser(
				message.sender(),
				"/private/reply",
				formattedMessage);

		sendAckToUser(principal, null, message.target(), message.clientMessageId());
	}

	private MessageDatabaseFormat addToDatabase(
			String key,
			Message message,
			ConcurrentHashMap<String, Deque<MessageDatabaseFormat>> database,
			ConcurrentHashMap<String, AtomicInteger> idCounters,
			ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> lastReadMap) {

		Deque<MessageDatabaseFormat> deque = database.computeIfAbsent(key, _ -> new ConcurrentLinkedDeque<>());
		int id = idCounters.computeIfAbsent(key, _ -> new AtomicInteger(0)).getAndIncrement();

		MessageDatabaseFormat formattedMessage = new MessageDatabaseFormat(
				id,
				message.content(),
				message.sender(),
				message.target(),
				LocalDateTime.now().format(FORMATTER),
				System.currentTimeMillis());

		deque.add(formattedMessage);

		lastReadMap.computeIfAbsent(key, _ -> new ConcurrentHashMap<>())
				.merge(message.sender(), id, Math::max);

		return formattedMessage;
	}


	// TODO: add logging for bug fixing
	public void markReadUpTo(String conversationKey, String username, int upToMessageId, boolean isGroup) {
		if (username == null || username.isEmpty())
			return;

		var map = isGroup ? groupLastRead : privateLastRead;
		var lastReadForConversation = map.computeIfAbsent(conversationKey, _ -> new ConcurrentHashMap<>());

		lastReadForConversation.merge(username, upToMessageId, Math::max);
	}

	public int getUnreadCount(String conversationKey, String username, boolean isGroup) {
		var database = isGroup ? groupMessages : privateMessages;
		var deque = database.get(conversationKey);
		if (deque == null || deque.isEmpty())
			return 0;

		int maxId = deque.peekLast().id();
		var lastReadMap = (isGroup ? groupLastRead : privateLastRead)
				.getOrDefault(conversationKey, new ConcurrentHashMap<>());
		int lastRead = lastReadMap.getOrDefault(username, -1);

		return Math.max(0, (maxId - lastRead));
	}

	// Make sure that "student1" -> "student2" and "student2" -> "student1" has
	// the same key (key: "student1-student2").
	public static String getConversationKey(String user1, String user2) {
		if (user1.compareTo(user2) < 0) {
			return user1 + "-" + user2;
		} else {
			return user2 + "-" + user1;
		}
	}

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
