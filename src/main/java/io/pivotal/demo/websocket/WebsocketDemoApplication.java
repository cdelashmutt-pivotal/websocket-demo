package io.pivotal.demo.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class WebsocketDemoApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(
			SpringApplicationBuilder application) {
		return application.sources(WebsocketDemoApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(WebsocketDemoApplication.class, args);
	}
}
