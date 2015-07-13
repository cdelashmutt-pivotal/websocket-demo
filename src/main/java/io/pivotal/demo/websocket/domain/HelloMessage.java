package io.pivotal.demo.websocket.domain;

public class HelloMessage {

	public String name;

	public HelloMessage()
	{
		super();
	}
	
	public HelloMessage(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
