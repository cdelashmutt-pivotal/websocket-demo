package io.pivotal.demo.websocket.pureunit;

import io.pivotal.demo.websocket.domain.Greeting;
import io.pivotal.demo.websocket.domain.HelloMessage;
import io.pivotal.demo.websocket.service.GreetingServiceImpl;

import java.util.ArrayList;
import java.util.List;

public class TrackingGreetingService 
extends GreetingServiceImpl {

	private List<Greeting> greetings = new ArrayList<Greeting>();
	
	@Override
	public Greeting getGreeting(HelloMessage message) {
		Greeting greeting = super.getGreeting(message);
		greetings.add(greeting);
		return greeting;
	}
	
	public List<Greeting> getGreetings()
	{
		return greetings;
	}

}
