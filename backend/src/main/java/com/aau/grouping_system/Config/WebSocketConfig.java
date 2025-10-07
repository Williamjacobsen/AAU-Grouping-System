package com.aau.grouping_system.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	// How it works:
	// Client establish their connection at: /ws
	// Clients sends their messages through /app/... (/app is a prefix / the channel
	// for incoming messages)
	// Server will broadcast / publish through /topic/...

	// A task schedular is need for the heartbeats,
	// otherwise the server would not be able to send a heartbeat a 10 seconds.
	private TaskScheduler messageBrokerTaskScheduler;

	@Autowired // needed for spring boot to auto inject.
	// @Lazy, just means that it will be initalized when needed, instead of at startup.
	public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
		this.messageBrokerTaskScheduler = taskScheduler;
	}

	@Override
	public void registerStompEndpoints(@SuppressWarnings("null") StompEndpointRegistry registry) {
		registry.addEndpoint("/ws")
				.setAllowedOriginPatterns("*")
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(@SuppressWarnings("null") MessageBrokerRegistry registry) {
		// Regarding how heartbeats work: (the line below)
		// The server sends a heartbeat every 10 seconds and expects a response from the
		// client every 20 seconds.
		// This is to prevent stale session from hogging up the backend resources.
		registry.enableSimpleBroker("/topic")
				.setHeartbeatValue(new long[] { 10_000, 20_000 })
				.setTaskScheduler(this.messageBrokerTaskScheduler);
		registry.setApplicationDestinationPrefixes("/app");
	}
}