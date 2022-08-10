package com.howtodoinjava.securitymodule.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.howtodoinjava.securitymodule.service.AuthenticationService;
import com.howtodoinjava.securitymodule.service.AuthenticationServiceImpl;

@Configuration
@ConditionalOnClass(AuthenticationService.class)
public class SecurityAutoConfiguration {

	@ConditionalOnMissingBean
	@Bean
	public AuthenticationService callAuth() {
		return new AuthenticationServiceImpl();
	}
}