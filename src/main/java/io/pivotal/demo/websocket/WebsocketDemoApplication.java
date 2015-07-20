package io.pivotal.demo.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(basePackageClasses = WebsocketDemoApplication.class, 
	excludeFilters = { @ComponentScan.Filter(type = FilterType.REGEX, pattern = "io\\.pivotal\\.demo\\.websocket\\.tests\\..*") })
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
