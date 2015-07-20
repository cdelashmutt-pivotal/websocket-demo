package io.pivotal.demo.websocket.controller;

import io.pivotal.demo.websocket.domain.Greeting;
import io.pivotal.demo.websocket.domain.HelloMessage;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class GreetingController {

	@Resource
	private SimpMessagingTemplate messageTemplate;

	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message)
	throws Exception
	{
		return buildGreeting(message);
	}

	private Greeting buildGreeting(HelloMessage message) {
		return new Greeting("Hello, " + message.getName() + "!");
	}
	
	/**
	 * Allows non websocket clients to send messages.
	 * 
	 * @param message The message to send.
	 * @return http response code.
	 */
	@RequestMapping(value="/postMessage",method=RequestMethod.POST)
	public ResponseEntity<String> postMessage(@RequestBody HelloMessage message)
	{
		messageTemplate.convertAndSend("/topic/greetings", buildGreeting(message));
		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
