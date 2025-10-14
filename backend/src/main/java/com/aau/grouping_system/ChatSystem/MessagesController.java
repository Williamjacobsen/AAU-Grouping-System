package com.aau.grouping_system.ChatSystem;

import java.util.Deque;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessagesController {
	private final WebSocketController webSocketController;

	public MessagesController(WebSocketController webSocketController) {
		this.webSocketController = webSocketController;
	}

	@GetMapping("/group/{groupId}/messages/get/all")
	public Deque<WebSocketController.groupMessage> getGroupMessage(@PathVariable String groupId) {
		return webSocketController.groupMessages.get(groupId);
	}

}
