package io.pivotal.demo.websocket.tests.contextunit;

import static org.junit.Assert.*;
import io.pivotal.demo.websocket.domain.HelloMessage;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple class that uses the Spring context as part of the test, and injects interceptors to listen for messages for validation in a test.
 * 
 * This type of a test is much slower than a pure unit test, as it loads the app context up.
 * 
 * @author grog
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
		WebsocketDemoApplicationTests.TestWebSocketConfig.class,
		WebsocketDemoApplicationTests.TestConfig.class })
public class WebsocketDemoApplicationTests {

	@Autowired
	private AbstractSubscribableChannel clientInboundChannel;

	@Autowired
	private AbstractSubscribableChannel clientOutboundChannel;

	@Autowired
	private AbstractSubscribableChannel brokerChannel;

	private TestChannelInterceptor brokerChannelInterceptor;

	@Before
	public void setUp() throws Exception {

		this.brokerChannelInterceptor = new TestChannelInterceptor();

		this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
	}

	@Test
	public void getGreeting() throws Exception {

		HelloMessage helloMessage = new HelloMessage("grog");

		byte[] payload = new ObjectMapper().writeValueAsBytes(helloMessage);

		StompHeaderAccessor headers = StompHeaderAccessor
				.create(StompCommand.SEND);
		headers.setDestination("/app/hello");
		headers.setSessionId("0");
		headers.setSessionAttributes(new HashMap<String, Object>());
		Message<byte[]> message = MessageBuilder.createMessage(payload,
				headers.getMessageHeaders());

		this.brokerChannelInterceptor.setIncludedDestinations("/topic/**");
		this.clientInboundChannel.send(message);

		Message<?> greetingResponse = this.brokerChannelInterceptor
				.awaitMessage(5);
		assertNotNull(greetingResponse);

		StompHeaderAccessor greetingResponseHeaders = StompHeaderAccessor
				.wrap(greetingResponse);
		assertEquals("/topic/greetings",
				greetingResponseHeaders.getDestination());

		String json = new String((byte[]) greetingResponse.getPayload(),
				Charset.forName("UTF-8"));
		new JsonPathExpectationsHelper("$.content").assertValue(json,
				"Hello, grog!");
	}

	/**
	 * A simple config to pickup all the app components, but override the way
	 * that Websockets and messaging is configured for the app.
	 * 
	 * @author grog
	 *
	 */
	@Configuration
	@EnableScheduling
	@ComponentScan(basePackages = "io.pivotal.demo.websocket", 
	  excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class))
	@EnableWebSocketMessageBroker
	static class TestWebSocketConfig extends
			AbstractWebSocketMessageBrokerConfigurer {

		@Autowired
		Environment env;

		@Override
		public void registerStompEndpoints(StompEndpointRegistry registry) {
			registry.addEndpoint("/hello").withSockJS();
		}

		@Override
		public void configureMessageBroker(MessageBrokerRegistry registry) {
			registry.enableStompBrokerRelay("/queue/", "/topic/");
			registry.setApplicationDestinationPrefixes("/app");
		}
	}

	/**
	 * Configuration class that un-registers MessageHandler's it finds in the
	 * ApplicationContext from the message channels they are subscribed to...
	 * except the message handler used to invoke annotated message handling
	 * methods. The intent is to reduce additional processing and additional
	 * messages not related to the test.
	 */
	@Configuration
	static class TestConfig implements
			ApplicationListener<ContextRefreshedEvent> {

		@Autowired
		private List<SubscribableChannel> channels;

		@Autowired
		private List<MessageHandler> handlers;

		@Override
		public void onApplicationEvent(ContextRefreshedEvent event) {
			for (MessageHandler handler : handlers) {
				if (handler instanceof SimpAnnotationMethodMessageHandler) {
					continue;
				}
				for (SubscribableChannel channel : channels) {
					channel.unsubscribe(handler);
				}
			}
		}
	}
}