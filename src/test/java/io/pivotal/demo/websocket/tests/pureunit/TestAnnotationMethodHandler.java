package io.pivotal.demo.websocket.tests.pureunit;

import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;

/**
 * An extension of SimpAnnotationMethodMessageHandler that exposes a (public)
 * method for manually registering a controller, rather than having it
 * auto-discovered in the Spring ApplicationContext.
 * 
 * This is to allow discovery of annotations, and have the message system
 * configure itself appropriately
 */
public class TestAnnotationMethodHandler
extends SimpAnnotationMethodMessageHandler {
	public TestAnnotationMethodHandler(SubscribableChannel inChannel,
			MessageChannel outChannel,
			SimpMessageSendingOperations brokerTemplate) {

		super(inChannel, outChannel, brokerTemplate);
	}

	public void registerHandler(Object handler) {
		super.detectHandlerMethods(handler);
	}

}
