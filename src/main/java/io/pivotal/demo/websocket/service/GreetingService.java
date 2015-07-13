package io.pivotal.demo.websocket.service;

import io.pivotal.demo.websocket.domain.Greeting;
import io.pivotal.demo.websocket.domain.HelloMessage;

public interface GreetingService {

	Greeting getGreeting(HelloMessage message);

}
