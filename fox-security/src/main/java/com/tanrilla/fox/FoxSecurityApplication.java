package com.tanrilla.fox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@SpringBootApplication
public class FoxSecurityApplication {

	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@RequestMapping("/resource")
	public Map<String,Object> home() {
		Map<String,Object> model = new HashMap<>();
		model.put("id", UUID.randomUUID().toString());
		model.put("content", "Hello World, I'm a protected resource");
		return model;
	}

	@RequestMapping("/public")
	public Map<String,Object> publicEndpoint() {
		Map<String,Object> model = new HashMap<>();
		model.put("content", "Hello World, I'm public!");
		return model;
	}

	@Configuration
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					.httpBasic()
					.and()
					.authorizeRequests()
					.antMatchers("/public").permitAll()
					.anyRequest().authenticated()
					// TODO Study CSRF
					.and().csrf()
					.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(FoxSecurityApplication.class, args);
	}

}
