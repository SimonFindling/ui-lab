package de.hska.uilab.mefiro.api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ApiGateway {

	public static void main(String[] args) {
		SpringApplication.run(ApiGateway.class, args);
	}
}
