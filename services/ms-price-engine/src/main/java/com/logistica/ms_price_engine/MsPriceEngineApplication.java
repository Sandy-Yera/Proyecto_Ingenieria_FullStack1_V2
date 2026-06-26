package com.logistica.ms_price_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsPriceEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsPriceEngineApplication.class, args);
	}

}
