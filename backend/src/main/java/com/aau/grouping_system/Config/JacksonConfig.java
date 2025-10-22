package com.aau.grouping_system.Config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.SerializationFeature;

/// Jackson is the framework that Spring Boot uses for reading and writing JSON.
@Configuration
public class JacksonConfig {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> {
			// Disable the throwing of errors when an object with no publicly accessible
			// fields or getters is sent via JSON.
			builder.featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		};
	}
}
