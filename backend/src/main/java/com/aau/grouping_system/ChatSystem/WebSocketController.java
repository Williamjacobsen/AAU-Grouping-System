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

	public record groupMessage(Integer id, String content, String sender, String time) {
	};

	public final ConcurrentHashMap<String, Deque<groupMessage>> groupMessages = new ConcurrentHashMap<>();
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
	public void sendMessage(@DestinationVariable String groupId, Message message, Principal principal) {
		System.out.println("Received group message from: " + (principal != null ? principal.getName() : "UNKNOWN"));

		groupMessages
				.computeIfAbsent(groupId, _ -> new ConcurrentLinkedDeque<>())
				.add(new groupMessage(groupMessages.get(groupId).size(), message.content(), message.sender(), LocalDateTime.now().format(FORMATTER)));

		messagingTemplate.convertAndSend(
				"/group/" + groupId + "/messages",
				new Message(message.content(), message.sender(), null, message.clientMessageId()));

		sendAckToUser(principal, groupId, null, message.clientMessageId());
	}

	@MessageMapping("/private/send")
	public void sendPrivateMessage(Message message, Principal principal) {
		System.out.println("Private message from " + (principal != null ? principal.getName() : "UNKNOWN")
				+ " to " + message.target());

		if (message.target() != null && !message.target().isEmpty()) {
			messagingTemplate.convertAndSendToUser(
					message.target(),
					"/private/reply",
					new Message(message.content(), message.sender(), message.target(), message.clientMessageId()));

			sendAckToUser(principal, null, message.target(), message.clientMessageId());

			System.out.println("Sent private message to: " + message.target());
		} else {
			System.out.println("ERROR: No target specified for private message");
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