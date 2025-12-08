package com.aau.grouping_system.ChatSystem;

import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.aau.grouping_system.Database.DatabaseItem;
import com.aau.grouping_system.ChatSystem.WebSocket.WebSocketService.MessageDatabaseFormat;

public class ChatRoom extends DatabaseItem {

	private final Deque<MessageDatabaseFormat> messages = new ConcurrentLinkedDeque<>();
	private final ConcurrentHashMap<String, Integer> lastRead = new ConcurrentHashMap<>();

	private int nextMessageId = 0;

	private final String conversationKey;
	private final boolean isGroup;

	public ChatRoom(String conversationKey, boolean isGroup) {
		this.conversationKey = conversationKey;
		this.isGroup = isGroup;
	}

	public String getConversationKey() {
		return conversationKey;
	}

	public boolean isGroup() {
		return isGroup;
	}

	public Deque<MessageDatabaseFormat> getMessages() {
		return messages;
	}

	public ConcurrentHashMap<String, Integer> getLastReadMap() {
		return lastRead;
	}

	// This method replaces direct access to the AtomicInteger (it is not Serializable)
	public synchronized int getAndIncrementNextId() {
		return nextMessageId++;
	}
}