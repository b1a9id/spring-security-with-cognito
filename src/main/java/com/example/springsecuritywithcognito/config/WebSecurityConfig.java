package com.example.springsecuritywithcognito.config;

import com.example.springsecuritywithcognito.props.CognitoProps;
import com.example.springsecuritywithcognito.security.UserAuthenticatedVoter;
import com.example.springsecuritywithcognito.security.UserAuthenticationProvider;
import com.example.springsecuritywithcognito.security.filter.JWTAuthenticationFilter;
import com.example.springsecuritywithcognito.security.filter.JWTAuthorizationFilter;
import com.example.springsecuritywithcognito.service.AuthenticatedUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserAuthenticationProvider userAuthenticationProvider;
	private final AuthenticatedUserDetailsService userDetailsService;
	private final AuthenticationSuccessHandler successHandler;
	private final AuthenticationFailureHandler failureHandler;
	private final CognitoProps cognitoProps;

	public WebSecurityConfig(
			UserAuthenticationProvider userAuthenticationProvider,
			AuthenticatedUserDetailsService userDetailsService,
			AuthenticationSuccessHandler successHandler,
			AuthenticationFailureHandler failureHandler,
			CognitoProps cognitoProps) {
		this.userAuthenticationProvider = userAuthenticationProvider;
		this.userDetailsService = userDetailsService;
		this.successHandler = successHandler;
		this.failureHandler = failureHandler;
		this.cognitoProps = cognitoProps;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
					.antMatchers("/users/**").authenticated()
					.antMatchers("/users/change-password/**").anonymous()
					.anyRequest().permitAll()
					.accessDecisionManager(accessDecisionManager())
				.and()
					.logout()
					.permitAll()
				.and()
					.csrf().disable()
					.addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
					.addFilter(jwtAuthorizationFilter());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider)
				.userDetailsService(userDetailsService);
	}

	@Bean
	public JWTAuthenticationFilter jwtAuthenticationFilter() throws Exception {
		JWTAuthenticationFilter filter = new JWTAuthenticationFilter();
		filter.setRequiresAuthenticationRequestMatcher(
				new AntPathRequestMatcher("/users/authentication", HttpMethod.POST.name()));
		filter.setAuthenticationSuccessHandler(successHandler);
		filter.setAuthenticationFailureHandler(failureHandler);
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}

	@Bean
	public JWTAuthorizationFilter jwtAuthorizationFilter() throws Exception {
		return new JWTAuthorizationFilter(authenticationManager(), userDetailsService, cognitoProps);
	}

	@Bean
	public AccessDecisionManager accessDecisionManager() {
		return new UnanimousBased(
				Arrays.asList(
						new WebExpressionVoter(),
						new UserAuthenticatedVoter(cognitoProps, userDetailsService, authenticationTrustResolver())));
	}

	@Bean
	public AuthenticationTrustResolver authenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
	}
}
