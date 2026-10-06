package com.lidigu.studentManagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.lidigu.studentManagement.service.CustomUserDetailsService;
import com.lidigu.studentManagement.service.StudentService;
import com.lidigu.studentManagement.service.TeacherService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class DemoSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
				.userDetailsService(customUserDetailsService)
				.passwordEncoder(passwordEncoder());

		auth.inMemoryAuthentication() // admin password username
				.withUser("admin")
				.password(passwordEncoder().encode("1"))
				.roles("ADMIN");

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/").authenticated()
				.antMatchers("/admin/**").hasRole("ADMIN") // user with student or teacher role cannot access url
															// starting with admin
				.antMatchers("/student/**").hasRole("STUDENT")
				.antMatchers("/teacher/**").hasRole("TEACHER")
				.antMatchers("/reports/create", "/reports/save", "/reports/my").hasRole("STUDENT")
				.antMatchers("/reports/all", "/reports/view/**", "/reports/**/status", "/reports/**/comment", "/reports/export/**")
				.hasAnyRole("TEACHER", "ADMIN")
				.antMatchers("/reports/**/approveAction", "/reports/**/rejectAction", "/reports/**/approveComment/**")
				.hasRole("ADMIN")
				.antMatchers("/reports/api/**").authenticated()
				.antMatchers("/lostfound/my", "/lostfound/create", "/lostfound/save", "/lostfound/delete/**")
				.hasRole("STUDENT")
				.antMatchers("/lostfound/all", "/lostfound/updateStatus")
				.hasAnyRole("TEACHER", "ADMIN")
				.antMatchers("/lostfound/view/**", "/lostfound/**/comment")
				.authenticated()
				.and()
				.formLogin()
				.loginPage("/showLoginPage") // custom login page is generated in LoginController
				.loginProcessingUrl("/authenticateTheUser") // authenticateTheUser is automatically done by spring boot
				.successHandler(customAuthenticationSuccessHandler) // after login, user is redirected to home page
																	// depending on the role.
				.permitAll()
				.and()
				.logout().permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/access-denied");

	}

	// needed for admin password encoding for security purposes
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
