package com.example.springsecuritywithcognito.config;

import com.example.springsecuritywithcognito.enums.Role;
import com.example.springsecuritywithcognito.props.CognitoProps;
import com.example.springsecuritywithcognito.security.core.authentication.UserAccessTokenAuthenticationProvider;
import com.example.springsecuritywithcognito.security.core.userdetails.CustomAuthenticationUserDetailsService;
import com.example.springsecuritywithcognito.security.core.userdetails.CustomUserDetailsService;
import com.example.springsecuritywithcognito.security.web.preauth.CustomPreAuthenticatedProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final CustomUserDetailsService userDetailsService;
	private final CustomAuthenticationUserDetailsService authenticationUserDetailsService;
	private final CognitoProps cognitoProps;

	public WebSecurityConfig(
			CustomUserDetailsService userDetailsService,
			CustomAuthenticationUserDetailsService authenticationUserDetailsService,
			CognitoProps cognitoProps) {
		this.userDetailsService = userDetailsService;
		this.authenticationUserDetailsService = authenticationUserDetailsService;
		this.cognitoProps = cognitoProps;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
					.antMatchers("/users/**").hasRole(Role.STAFF.name())
					.antMatchers("/change-password/**", "/authentication").anonymous()
					.anyRequest().permitAll()
				.and()
					.logout()
					.permitAll()
				.and()
					.csrf().disable()
					.addFilter(preAuthenticatedProcessingFilter())
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAccessTokenAuthenticationProvider())
				.userDetailsService(userDetailsService);
	}

	@Bean
	public CustomPreAuthenticatedProcessingFilter preAuthenticatedProcessingFilter() throws Exception {
		CustomPreAuthenticatedProcessingFilter filter = new CustomPreAuthenticatedProcessingFilter();
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}

	@Bean
	public AuthenticationTrustResolver authenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
	}

	@Bean
	public UserAccessTokenAuthenticationProvider userAccessTokenAuthenticationProvider() {
		UserAccessTokenAuthenticationProvider provider = new UserAccessTokenAuthenticationProvider(cognitoProps);
		provider.setUserDetailsService(authenticationUserDetailsService);
		return provider;
	}
}
