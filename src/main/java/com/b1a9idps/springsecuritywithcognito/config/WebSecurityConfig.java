package com.b1a9idps.springsecuritywithcognito.config;

import com.b1a9idps.springsecuritywithcognito.enums.Role;
import com.b1a9idps.springsecuritywithcognito.props.CognitoProps;
import com.b1a9idps.springsecuritywithcognito.security.core.authentication.UserAccessTokenAuthenticationProvider;
import com.b1a9idps.springsecuritywithcognito.security.core.userdetails.CustomAuthenticationUserDetailsService;
import com.b1a9idps.springsecuritywithcognito.security.core.userdetails.CustomUserDetailsService;
import com.b1a9idps.springsecuritywithcognito.security.web.preauth.CustomPreAuthenticatedProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
					.antMatchers("/change-password/**", "/login").anonymous()
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
	public UserAccessTokenAuthenticationProvider userAccessTokenAuthenticationProvider() {
		UserAccessTokenAuthenticationProvider provider = new UserAccessTokenAuthenticationProvider(cognitoProps);
		provider.setUserDetailsService(authenticationUserDetailsService);
		return provider;
	}
}
