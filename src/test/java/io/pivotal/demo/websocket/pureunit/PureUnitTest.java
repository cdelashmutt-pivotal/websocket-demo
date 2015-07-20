package io.pivotal.demo.websocket.pureunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.pivotal.demo.websocket.controller.GreetingController;
import io.pivotal.demo.websocket.domain.HelloMessage;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.web.socket.test.StompMessageBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Non spring context related unit test to test out a WebSocket service
 */
public class PureUnitTest {

	private TestMessageChannel clientOutboundChannel;

	private TestMessageChannel clientInboundChannel;

	private TestAnnotationMethodHandler annotationMethodHandler;

	/**
	 * This method sets up a simple outbound and inbound channel with payload
	 * conversion to JSON
	 */
	@Before
	public void setup() {

		// The controller under test.
		GreetingController controller = new GreetingController();

		// Set up the outbound and inbound channels for catching messages.
		this.clientOutboundChannel = new TestMessageChannel();
		this.clientInboundChannel = new TestMessageChannel();
		SimpMessagingTemplate template = new SimpMessagingTemplate(
				clientInboundChannel);
		// Need to set the MessageConverter on the template to convert inbound
		// messages to JSON
		template.setMessageConverter(new MappingJackson2MessageConverter());

		this.annotationMethodHandler = new TestAnnotationMethodHandler(
				new TestMessageChannel(), clientOutboundChannel, template);

		this.annotationMethodHandler.registerHandler(controller);
		this.annotationMethodHandler
				.setMessageConverter(new MappingJackson2MessageConverter());
		this.annotationMethodHandler
				.setApplicationContext(new StaticApplicationContext());

		// This prefix should be prepended to any STOMP message destination that
		// we're trying to send to an @SubscribeMessage or @MessageMapping
		// method
		this.annotationMethodHandler.setDestinationPrefixes(Arrays
				.asList("/app"));

		// Let the handler do any post setup work it needs to do
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

		// Build and pass the message to the controller
		Message<byte[]> message = MessageBuilder.withPayload(payload)
				.setHeaders(headers).build();
		this.annotationMethodHandler.handleMessage(message);

		// Inbound channel will have our response message.
		assertEquals(1, this.clientInboundChannel.getMessages().size());

		Message<?> greeting = this.clientInboundChannel.getMessages().get(0);
		assertNotNull(greeting);

		// Make sure to check the destination is proper
		StompHeaderAccessor greetingResponseHeaders = StompHeaderAccessor
				.wrap(greeting);
		assertEquals("/topic/greetings",
				greetingResponseHeaders.getDestination());

		// Get the JSON response and validate it.
		String json = new String((byte[]) greeting.getPayload(),
				Charset.forName("UTF-8"));
		new JsonPathExpectationsHelper("$.content").assertValue(json,
				"Hello, grog!");
	}

	@Test
	public void getGreeting2() throws Exception {

		Message<byte[]> message = StompMessageBuilder
				.forCommand(StompCommand.SEND)
				.sessionId("0")
				.destination("/app/hello")
				.payload(new HelloMessage("grog"))
				.build();

		this.annotationMethodHandler.handleMessage(message);

		assertEquals(1, this.clientInboundChannel.getMessages().size());

		Message<?> greeting = this.clientInboundChannel.getMessages().get(0);
		assertNotNull(greeting);

		StompHeaderAccessor greetingResponseHeaders = StompHeaderAccessor
				.wrap(greeting);
		assertEquals("/topic/greetings",
				greetingResponseHeaders.getDestination());

		String json = new String((byte[]) greeting.getPayload(),
				Charset.forName("UTF-8"));
		new JsonPathExpectationsHelper("$.content").assertValue(json,
				"Hello, grog!");
	}

}
