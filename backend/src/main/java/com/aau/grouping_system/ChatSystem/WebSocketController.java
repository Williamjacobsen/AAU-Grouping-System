package com.aau.grouping_system.ChatSystem;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	public record MessageDatabaseFormat(Integer id, String content, String sender, String target, String time) {
	};

	public final ConcurrentHashMap<String, Deque<MessageDatabaseFormat>> groupMessages = new ConcurrentHashMap<>();
	public final ConcurrentHashMap<String, Deque<MessageDatabaseFormat>> privateMessages = new ConcurrentHashMap<>();
	/*
	 * Looks like this:
	 * {
	 * "group 1": [
	 * {sender: "student 1", content: "bla bla"},
	 * {sender: "student 2", content: "bla bla bla"},
	 * ],
	 * "group 2": [
	 * {sender: "student 1", content: "bla bla"},
	 * {sender: "student 3", content: "bla bla bla"},
	 * {sender: "student 2", content: "bla"},
	 * ],
	 * }
	 * 
	 * In Group, to delete, just do groupMessages.remove(groupName)
	 */

	private final SimpMessagingTemplate messagingTemplate;
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	public WebSocketController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public record Message(String content, String sender, String target, String clientMessageId) {
	}

	@MessageMapping("/group/{groupId}/send")
	public void sendGroupMessage(@DestinationVariable String groupId, Message message, Principal principal) {
		System.out.println("Received group message from: " + (principal != null ? principal.getName() : "UNKNOWN"));

		// TODO: add input validation

		Deque<MessageDatabaseFormat> deque = groupMessages.computeIfAbsent(groupId, _ -> new ConcurrentLinkedDeque<>());

		MessageDatabaseFormat formattedMessage = new MessageDatabaseFormat(
				deque.size(),
				message.content(),
				message.sender(),
				message.target(),
				LocalDateTime.now().format(FORMATTER));

		deque.add(formattedMessage);

		messagingTemplate.convertAndSend("/group/" + groupId + "/messages", formattedMessage);

		sendAckToUser(principal, groupId, null, message.clientMessageId());
	}

	@MessageMapping("/private/send")
	public void sendPrivateMessage(Message message, Principal principal) {
		System.out.println("Private message from " + (principal != null ? principal.getName() : "UNKNOWN")
				+ " to " + message.target());

		if (message.target() == null || message.target().isEmpty()) { // TODO: improve input validation
			System.out.println("ERROR: No target specified for private message");
			return;
		}

		String key = getConversationKey(message.sender(), message.target());

		Deque<MessageDatabaseFormat> deque = privateMessages.computeIfAbsent(key, _ -> new ConcurrentLinkedDeque<>());

		MessageDatabaseFormat formattedMessage = new MessageDatabaseFormat(
				deque.size(),
				message.content(),
				message.sender(),
				message.target(),
				LocalDateTime.now().format(FORMATTER));

		deque.add(formattedMessage);

		messagingTemplate.convertAndSendToUser(
				message.target(),
				"/private/reply",
				formattedMessage);

		// This is not the best approch, i should add the message for the sender client side, when ack is received.
		messagingTemplate.convertAndSendToUser(
			message.sender(),
			"/private/reply",
			formattedMessage);

		sendAckToUser(principal, null, message.target(), message.clientMessageId());

		System.out.println("Sent private message to: " + message.target());
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

		messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/acks", ack);
	}
}