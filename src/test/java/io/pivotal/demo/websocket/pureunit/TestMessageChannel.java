package io.pivotal.demo.websocket.pureunit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.AbstractSubscribableChannel;

/**
 * This is a simple message channel to capture messages sent to it for later review in a test.
 *
 */
public class TestMessageChannel extends AbstractSubscribableChannel {

	private final List<Message<?>> messages = new ArrayList<>();


	public List<Message<?>> getMessages() {
		return this.messages;
	}

	@Override
	protected boolean sendInternal(Message<?> message, long timeout) {
		this.messages.add(message);
		return true;
	}

}