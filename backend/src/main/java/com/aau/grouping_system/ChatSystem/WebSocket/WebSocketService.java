package com.aau.grouping_system.ChatSystem.WebSocket;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public record Message(String content, String sender, String target, String clientMessageId) {
	}

	public record MessageDatabaseFormat(Integer id, String content, String sender, String target, String time) {
	};

	public final ConcurrentHashMap<String, Deque<MessageDatabaseFormat>> groupMessages = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<String, Deque<MessageDatabaseFormat>> privateMessages = new ConcurrentHashMap<>();
	/*
	 * Looks like this:
	 * {
	 * "group 1": [
	 * {id: 0, target: null, sender: "student 2", content: "bla bla bla", time: "2025-10-19 20:20"},
	 * {id: 1, target: null, sender: "student 1", content: "bla bla", time: "2025-10-19 20:25"},
	 * ],
	 * "group 2": [
	 * {id: 0, target: null, sender: "student 1", content: "bla bla", time: "2025-10-19 20:20"},
	 * {id: 1, target: null, sender: "student 3", content: "bla bla bla", time: "2025-10-19 20:25"},
	 * {id: 2, target: null, sender: "student 2", content: "bla", time: "2025-10-19 20:30"},
	 * ],
	 * }
	 * 
	 * In Group, to delete, just do groupMessages.remove(groupName)
	 */

	private final SimpMessagingTemplate ws;

	public WebSocketService(SimpMessagingTemplate ws) {
		this.ws = ws;
	}

	public void sendGroupMessage(String groupId, Message message, Principal principal) {
		MessageDatabaseFormat formattedMessage = addToDatabase(groupId, message);

		ws.convertAndSend("/group/" + groupId + "/messages", formattedMessage);

		sendAckToUser(principal, groupId, null, message.clientMessageId());
	}

	public void sendPrivateMessage(Message message, Principal principal) {
		String key = getConversationKey(message.sender(), message.target());

		MessageDatabaseFormat formattedMessage = addToDatabase(key, message);

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

	private MessageDatabaseFormat addToDatabase(String key, Message message) {
		Deque<MessageDatabaseFormat> deque = groupMessages.computeIfAbsent(key, _ -> new ConcurrentLinkedDeque<>());

		MessageDatabaseFormat formattedMessage = new MessageDatabaseFormat(
				deque.size(),
				message.content(),
				message.sender(),
				message.target(),
				LocalDateTime.now().format(FORMATTER));

		deque.add(formattedMessage);

		return formattedMessage;
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
