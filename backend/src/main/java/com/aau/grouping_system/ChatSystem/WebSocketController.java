package com.aau.grouping_system.ChatSystem;

import java.security.Principal;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

	public record groupMessage(String content, String sender) {
	};

	public final ConcurrentHashMap<String, Deque<groupMessage>> groupMessages = new ConcurrentHashMap<>();
	/*
	 * Looks like this:
	 * {
	 * 		"group 1": [
	 * 				{sender: "student 1", content: "bla bla"},
	 * 				{sender: "student 2", content: "bla bla bla"},
	 * 		],
	 * 	 "group 2": [
	 * 				{sender: "student 1", content: "bla bla"},
	 * 				{sender: "student 3", content: "bla bla bla"},
	 * 	  		{sender: "student 2", content: "bla"},
	 * 		],
	 * }
	 */

	private final SimpMessagingTemplate messagingTemplate;

	public WebSocketController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	public record Message(String content, String sender, String target) {
	}

	@MessageMapping("/group/{groupId}/send")
	@SendTo("/group/{groupId}/messages")
	public Message sendMessage(@DestinationVariable String groupId, Message message, Principal principal) {
		System.out.println("Received group message from: " + principal.getName());

		groupMessages
				.computeIfAbsent(groupId, _ -> new ConcurrentLinkedDeque<>())
				.add(new groupMessage(message.content(), message.sender()));

		return new Message(message.content, message.sender, null);
	}

	@MessageMapping("/private/send")
	public void sendPrivateMessage(Message message, Principal principal) {
		System.out.println("Private message from " + principal.getName() + " to " + message.target);

		if (message.target != null && !message.target.isEmpty()) {
			messagingTemplate.convertAndSendToUser(
					message.target,
					"/private/reply",
					new Message(message.content, message.sender, message.target));

			System.out.println("Sent private message to: " + message.target);
		} else {
			System.out.println("ERROR: No target specified for private message");
		}
	}

}