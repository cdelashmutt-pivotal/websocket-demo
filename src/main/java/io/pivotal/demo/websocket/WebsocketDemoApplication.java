package io.pivotal.demo.websocket;

import io.pivotal.demo.valve.BadValve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
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

	@Bean
	@ConditionalOnProperty(name="badvalve.enable", havingValue="true")
	public EmbeddedServletContainerFactory servletContainer() {
	    TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
	    tomcat.addContextValves(new BadValve());
	    return tomcat;
	}

}