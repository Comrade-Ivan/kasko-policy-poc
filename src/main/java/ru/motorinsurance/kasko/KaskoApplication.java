package ru.motorinsurance.kasko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {
		"ru.motorinsurance.kasko",
		"ru.motorinsurance.debeziumoutbox"
})
@EntityScan(basePackages = {
		"ru.motorinsurance.kasko",
		"ru.motorinsurance.debeziumoutbox"
})
public class KaskoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KaskoApplication.class, args);
	}

}
