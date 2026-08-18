package com.mekonnen.commercial_lending_platform;

import com.mekonnen.commercial_lending_platform.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class CommercialLendingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommercialLendingPlatformApplication.class, args);
	}

}
