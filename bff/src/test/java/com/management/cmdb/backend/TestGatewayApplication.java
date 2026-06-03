package com.management.cmdb.backend;

import org.springframework.boot.SpringApplication;

public class TestGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.from(CmdbBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
