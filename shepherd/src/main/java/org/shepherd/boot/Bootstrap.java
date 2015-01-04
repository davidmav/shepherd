package org.shepherd.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@ComponentScan(basePackages = { "org.shepherd" })
@EnableAutoConfiguration
@ImportResource("META-INF/bootstrap/*")
public class Bootstrap {

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(Bootstrap.class, args);
	}
}
