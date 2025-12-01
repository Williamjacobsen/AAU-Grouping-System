package com.aau.grouping_system.ChatSystem.WebSocket;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService.IncommingMessage;;

@Controller
public class WebSocketController {

	private final WebSocketService webSocketService;

	public WebSocketController(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	@MessageMapping("/group/{groupId}/send")
	public void sendGroupMessage(@DestinationVariable String groupId, IncommingMessage message, Principal principal) {
		System.out.println(String.format("Received Websocket: /group/%s/send", groupId));
		webSocketService.sendGroupMessage(groupId, message, principal);
	}

	@MessageMapping("/private/send")
	public void sendPrivateMessage(IncommingMessage message, Principal principal) {
		System.out.println("Received Websocket: /private/send");
		webSocketService.sendPrivateMessage(message, principal);
	}

	public record ReadUpToPayload(String conversationKey, String username, int upToMessageId) {
	}

	@MessageMapping("/group/{groupId}/readUpTo")
	public void readUpToGroup(@DestinationVariable String groupId, ReadUpToPayload payload, Principal principal) {
		System.out.println(String.format("Received Websocket: /group/%s/readUpTo", groupId));
		webSocketService.markReadUpTo(groupId, payload.username(), payload.upToMessageId(), true);
	}

	@MessageMapping("/private/readUpTo")
	public void readUpToPrivate(ReadUpToPayload payload, Principal principal) {
		System.out.println("Received Websocket: /private/readUpTo");
		webSocketService.markReadUpTo(payload.conversationKey(), payload.username(), payload.upToMessageId(), false);
	}

}