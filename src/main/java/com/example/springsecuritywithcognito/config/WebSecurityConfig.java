package com.example.springsecuritywithcognito.config;

import com.example.springsecuritywithcognito.enums.Role;
import com.example.springsecuritywithcognito.props.CognitoProps;
import com.example.springsecuritywithcognito.security.core.authentication.UserAccessTokenAuthenticationProvider;
import com.example.springsecuritywithcognito.security.core.authentication.UserAuthenticationProvider;
import com.example.springsecuritywithcognito.security.core.userdetails.CustomUserDetailsService;
import com.example.springsecuritywithcognito.security.web.authentication.CustomUsernamePasswordAuthenticationFilter;
import com.example.springsecuritywithcognito.security.web.preauth.CustomPreAuthenticatedProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserAuthenticationProvider userAuthenticationProvider;
	private final CustomUserDetailsService userDetailsService;
	private final AuthenticationSuccessHandler successHandler;
	private final AuthenticationFailureHandler failureHandler;
	private final CognitoProps cognitoProps;

	public WebSecurityConfig(
			UserAuthenticationProvider userAuthenticationProvider,
			CustomUserDetailsService userDetailsService,
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
					.antMatchers("/users/**").hasRole(Role.STAFF.name())
					.antMatchers("/change-password/**").anonymous()
					.anyRequest().permitAll()
				.and()
					.logout()
					.permitAll()
				.and()
					.csrf().disable()
					.addFilter(usernamePasswordAuthenticationFilter())
					.addFilter(preAuthenticatedProcessingFilter())
					.exceptionHandling()
					.authenticationEntryPoint(http403ForbiddenEntryPoint());
		http.exceptionHandling();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(userAuthenticationProvider)
				.authenticationProvider(userAccessTokenAuthenticationProvider())
				.userDetailsService(userDetailsService);
	}

	@Bean
	public CustomUsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter() throws Exception {
		CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
		filter.setRequiresAuthenticationRequestMatcher(
				new AntPathRequestMatcher("/users/authentication", HttpMethod.POST.name()));
		filter.setAuthenticationSuccessHandler(successHandler);
		filter.setAuthenticationFailureHandler(failureHandler);
		filter.setAuthenticationManager(authenticationManager());
		return filter;
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
		provider.setUserDetailsService(userDetailsService);
		return provider;
	}

	@Bean
	public AuthenticationEntryPoint http403ForbiddenEntryPoint() {
		return new Http403ForbiddenEntryPoint();
	}
}
