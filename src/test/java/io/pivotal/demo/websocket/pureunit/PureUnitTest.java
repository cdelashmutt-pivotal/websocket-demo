package io.pivotal.demo.websocket.pureunit;

import static org.junit.Assert.assertEquals;
import io.pivotal.demo.websocket.controller.GreetingController;
import io.pivotal.demo.websocket.domain.Greeting;
import io.pivotal.demo.websocket.domain.HelloMessage;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.JsonPathExpectationsHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Non spring context related unit test to test out a WebSocket service
 */
public class PureUnitTest {

	private TestMessageChannel clientOutboundChannel;

	private TestAnnotationMethodHandler annotationMethodHandler;

	private TrackingGreetingService trackingGreetingService = new TrackingGreetingService();

	@Before
	public void setup() {

		GreetingController controller = new GreetingController(
				trackingGreetingService);

		this.clientOutboundChannel = new TestMessageChannel();

		this.annotationMethodHandler = new TestAnnotationMethodHandler(
				new TestMessageChannel(), clientOutboundChannel,
				new SimpMessagingTemplate(new TestMessageChannel()));

		this.annotationMethodHandler.registerHandler(controller);
		this.annotationMethodHandler
				.setMessageConverter(new MappingJackson2MessageConverter());
		this.annotationMethodHandler
				.setApplicationContext(new StaticApplicationContext());

		// This value is arbitrary for this test. We'll use it later on in the
		// test method as the prefix to the MessageMapping we want to test.
		this.annotationMethodHandler.setDestinationPrefixes(Arrays
				.asList("/app"));

		this.annotationMethodHandler.afterPropertiesSet();
	}

	@Test
	public void getGreeting() throws Exception {

		StompHeaderAccessor headers = StompHeaderAccessor
				.create(StompCommand.SEND);
		headers.setSessionId("0");
		headers.setSessionAttributes(new HashMap<String, Object>());

		// Set this to the destination prefix specified above, and the value of
		// the MessageMapping for the method you want to invoke.
		headers.setDestination("/app/hello");

		// This will be the message payload you want to send.
		// Use "new byte[0]" if you want an empty message payload.
		//
		// Make sure this object can actually be properly deserialized from JSON
		// (default constructor exists, etc).
		byte[] payload = new ObjectMapper().writeValueAsBytes(new HelloMessage(
				"grog"));

		Message<byte[]> message = MessageBuilder.withPayload(payload)
				.setHeaders(headers).build();
		this.annotationMethodHandler.handleMessage(message);

		assertEquals(1, this.trackingGreetingService.getGreetings().size());

		Greeting greeting = this.trackingGreetingService.getGreetings().get(0);
		assertEquals(greeting.getContent(), "Hello, grog!");
	}

	/**
	 * An extension of SimpAnnotationMethodMessageHandler that exposes a
	 * (public) method for manually registering a controller, rather than having
	 * it auto-discovered in the Spring ApplicationContext.
	 * 
	 * This is to allow discovery of annotations, and have the message system
	 * configure itself appropriately
	 */
	private static class TestAnnotationMethodHandler extends
			SimpAnnotationMethodMessageHandler {

		public TestAnnotationMethodHandler(SubscribableChannel inChannel,
				MessageChannel outChannel,
				SimpMessageSendingOperations brokerTemplate) {

			super(inChannel, outChannel, brokerTemplate);
		}

		public void registerHandler(Object handler) {
			super.detectHandlerMethods(handler);
		}
	}

}
