package io.pivotal.demo.websocket.service;

import org.springframework.stereotype.Service;

import io.pivotal.demo.websocket.domain.Greeting;
import io.pivotal.demo.websocket.domain.HelloMessage;

@Service
public class GreetingServiceImpl implements GreetingService {

	@Override
	public Greeting getGreeting(HelloMessage message) {
		// TODO Auto-generated method stub
		return new Greeting("Hello, " + message.getName() + "!");
	}

}
