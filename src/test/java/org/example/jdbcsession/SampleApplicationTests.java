package org.example.jdbcsession;

import io.jmix.core.CoreConfiguration;
import io.jmix.data.DataConfiguration;
import io.jmix.security.SecurityConfiguration;
import io.jmix.sessions.SessionsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

//@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
		CoreConfiguration.class,
		DataConfiguration.class,
		SecurityConfiguration.class,
		SessionsConfiguration.class,
		SampleRestTestConfiguration.class})
@SpringBootTest(classes = SampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SampleApplicationTests {

	@Test
	void contextLoads() {
	}

}
