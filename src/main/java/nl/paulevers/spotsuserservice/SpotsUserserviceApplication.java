package nl.paulevers.spotsuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SpotsUserserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotsUserserviceApplication.class, args);
	}

}
