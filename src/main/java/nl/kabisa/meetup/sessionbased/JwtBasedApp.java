package nl.kabisa.meetup.sessionbased;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class JwtBasedApp {
    public static void main(String[] args) {
        SpringApplication.run(JwtBasedApp.class, args);
    }
}
