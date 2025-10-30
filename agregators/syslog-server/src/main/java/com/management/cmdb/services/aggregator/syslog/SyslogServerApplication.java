package com.management.cmdb.services.aggregator.syslog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class SyslogServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyslogServerApplication.class, args);
	}

}
