package nl.paulevers.spotsuserservice;

import nl.paulevers.spotsuserservice.controllers.UserController;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpotsUserserviceApplicationTests {

	@Autowired
	private UserController controller;

	@Test
	void contextLoads() {
		assertThat(controller).isNotNull();
	}

}
