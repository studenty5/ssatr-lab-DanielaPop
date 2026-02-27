package ro.utcn.ssatr.visitor_system_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VisitorSystemWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(VisitorSystemWebApplication.class, args);
	}
}