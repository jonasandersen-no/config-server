package configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

	public static void main(String[] args) {

		if (System.getProperty("HOSTNAME") == null){
			System.setProperty("spring.boot.admin.client.url", "http://cloud.bjoggis.com:8080");
		}

		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
