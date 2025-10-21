package com.aau.grouping_system.ChatSystem.WebSocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService.Message;

@Controller
public class WebSocketController {

	private final WebSocketService webSocketService;

	public WebSocketController(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	@MessageMapping("/group/{groupId}/send")
	public void sendGroupMessage(@DestinationVariable String groupId, Message message, Principal principal) {
		System.out.println("Received group message from: " + (principal != null ? principal.getName() : "UNKNOWN"));

		webSocketService.sendGroupMessage(groupId, message, principal);
	}

	@MessageMapping("/private/send")
	public void sendPrivateMessage(Message message, Principal principal) {
		System.out.println("Private message from " + (principal != null ? principal.getName() : "UNKNOWN")
				+ " to " + message.target());

		webSocketService.sendPrivateMessage(message, principal);

		System.out.println("Sent private message to: " + message.target());
	}

	public record ReadUpToPayload(String conversationKey, String username, int upToMessageId) {
	}

	@MessageMapping("/group/{groupId}/readUpTo")
	public void readUpToGroup(@DestinationVariable String groupId, ReadUpToPayload payload, Principal principal) {
		webSocketService.markReadUpTo(groupId, payload.username(), payload.upToMessageId(), true);
	}

	@MessageMapping("/private/readUpTo")
	public void readUpToPrivate(ReadUpToPayload payload, Principal principal) {
		webSocketService.markReadUpTo(payload.conversationKey(), payload.username(), payload.upToMessageId(), false);
	}

}