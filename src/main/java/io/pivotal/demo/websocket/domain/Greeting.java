package io.pivotal.demo.websocket.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Greeting {

	private String content;

	@JsonCreator
	public Greeting(@JsonProperty("content") String content) {
		super();
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
}