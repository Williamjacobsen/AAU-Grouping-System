package com.aau.grouping_system.Config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component 
public class AuthInterceptor implements HandshakeInterceptor {

	@SuppressWarnings("null")
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Map<String, Object> attributes) {
		// TODO: Authentication client here...
		
		System.out.println("beforeHandshake function has run...");

		return true;
	}

	@SuppressWarnings("null")
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Exception exception) {
	}
}