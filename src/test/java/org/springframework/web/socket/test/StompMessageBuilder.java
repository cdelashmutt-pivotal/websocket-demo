package org.springframework.web.socket.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StompMessageBuilder {

	private StompCommand command;
	private Object payload;
	private String sessionId;
	private String destination;
	private Map<String, Object> sessionAttributes = new HashMap<String,Object>();

	public StompMessageBuilder(StompCommand command) {
		this.command = command;
	}

	public static StompMessageBuilder forCommand(StompCommand command) {
		return new StompMessageBuilder(command);
	}

	public Message<byte[]> build() {
		try {
			StompHeaderAccessor headers = StompHeaderAccessor.create(command);
			headers.setSessionId(sessionId);
			headers.setDestination(destination);
			headers.setSessionAttributes(sessionAttributes);
			byte[] payloadData = new ObjectMapper().writeValueAsBytes(payload);
			return MessageBuilder.withPayload(payloadData).setHeaders(headers)
					.build();
		} catch (Exception e) {
			if (e instanceof RuntimeException)
				throw (RuntimeException) e;
			else
				throw new RuntimeException(e);
		}
	}

	public StompMessageBuilder payload(Object obj) {
		this.payload = obj;
		return this;
	}
	
	public StompMessageBuilder sessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

	public StompMessageBuilder destination(String destination) {
		this.destination = destination;
		return this;
	}

}
