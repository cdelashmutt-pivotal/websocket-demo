package io.pivotal.demo.websocket.controller;

import io.pivotal.demo.websocket.domain.Greeting;
import io.pivotal.demo.websocket.domain.HelloMessage;
import io.pivotal.demo.websocket.service.GreetingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	private GreetingService service;
	
	@Autowired
	public GreetingController(GreetingService service)
	{
		this.service = service;
	}
	
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message)
	throws Exception
	{
		return service.getGreeting(message);
	}
}
