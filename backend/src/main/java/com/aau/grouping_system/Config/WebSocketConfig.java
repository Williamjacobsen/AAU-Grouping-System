package com.aau.grouping_system.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
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
	// @Lazy, just means that it will be initalized when needed, instead of at
	// startup, it is also required for some reason.
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
		registry.enableSimpleBroker("/group", "/private")
				.setHeartbeatValue(new long[] { 10_000, 20_000 })
				.setTaskScheduler(this.messageBrokerTaskScheduler);
		registry.setApplicationDestinationPrefixes("/chat");
		registry.setUserDestinationPrefix("/user");

		// TODO: Should the other channels also have a heartbeat
	}

	@SuppressWarnings("null")
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
					String username = accessor.getFirstNativeHeader("username");

					if (username != null) {
						accessor.setUser(() -> username);
						System.out.println("User connected: " + username);
					}
				}

				return message;
			}
		});
	}
}