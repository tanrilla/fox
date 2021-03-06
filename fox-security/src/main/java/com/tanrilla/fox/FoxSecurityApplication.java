package com.tanrilla.fox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@SpringBootApplication
public class FoxSecurityApplication {

	// ************ Controllers ************
	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

	@RequestMapping("/resourceAdmin")
	public Map<String,Object> resourceAdmin() {
		Map<String,Object> model = new HashMap<>();
		model.put("id", UUID.randomUUID().toString());
		model.put("content", "Hello World, I'm a protected resource (only readable for admins)");
		return model;
	}

	@RequestMapping("/resource")
	public Map<String,Object> resource() {
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

	@RequestMapping("/token")
	public Map<String,String> token(HttpSession session) {
		return Collections.singletonMap("token", session.getId());
	}

	// ************ Security ************
	@EnableWebSecurity
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		/**
		 * TODO Support users from database instead of inMemory
		 * */
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(users());
		}

		@Bean
		public UserDetailsService users() {
			UserDetails user = User.builder()
					.username("user")
					//.password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
					.password("{noop}password")
					.roles("USER")
					.build();
			UserDetails admin = User.builder()
					.username("admin")
					//.password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
					.password("{noop}password")
					.roles("USER", "ADMIN")
					.build();
			return new InMemoryUserDetailsManager(user, admin);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
					//.cors()
					//.and()
					//.httpBasic().disable()
					.httpBasic()
					.and()
					.authorizeRequests()
					.antMatchers(HttpMethod.GET, "/public").permitAll()
					.antMatchers(HttpMethod.GET, "/resourceAdmin").hasRole("ADMIN")
					.antMatchers(HttpMethod.GET, "/resource").hasRole("USER")
					.anyRequest().authenticated();
					// TODO Study CSRF
					//.and().csrf()
					//.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
		}
	}

	// ************ App ************
	public static void main(String[] args) {
		SpringApplication.run(FoxSecurityApplication.class, args);
	}

}
