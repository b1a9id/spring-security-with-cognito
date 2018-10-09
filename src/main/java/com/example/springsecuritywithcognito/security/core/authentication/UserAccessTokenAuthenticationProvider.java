package com.example.springsecuritywithcognito.security.core.authentication;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecuritywithcognito.entity.User;
import com.example.springsecuritywithcognito.props.CognitoProps;
import com.example.springsecuritywithcognito.security.core.userdetails.CustomUserDetails;
import com.example.springsecuritywithcognito.utils.JWTUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.util.ObjectUtils.nullSafeEquals;

public class UserAccessTokenAuthenticationProvider implements AuthenticationProvider {

	private final CognitoProps cognitoProps;
	private UserDetailsService userDetailsService;

	public UserAccessTokenAuthenticationProvider(CognitoProps cognitoProps) {
		this.cognitoProps = cognitoProps;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(AccessTokenAuthenticationToken.class, authentication);

		String accessToken = Optional.ofNullable(authentication.getPrincipal())
				.map(Object::toString)
				.orElse(null);
		if (accessToken == null) {
			throw new BadCredentialsException("access token not found.");
		}

		DecodedJWT decodedAccessToken = JWTUtils.decode(accessToken);
		if (decodedAccessToken == null) {
			throw new BadCredentialsException("access token invalid.");
		}

		if (invalidAccessToken(decodedAccessToken)) {
			throw new BadCredentialsException("access token invalid");
		}

		if (isKidInvalid(decodedAccessToken)) {
			throw new BadCredentialsException("kid invalid");
		}

		if (isIssuerInvalid(decodedAccessToken)) {
			throw  new BadCredentialsException("issuer invalid");
		}

		String username = decodedAccessToken.getClaim(SPRING_SECURITY_FORM_USERNAME_KEY).asString();
		User user = getUser(username)
				.orElseThrow(() -> new UsernameNotFoundException("username '" + username + "' not found"));
		UserDetails userDetails = new CustomUserDetails(user, accessToken);
		return new AccessTokenAuthenticationToken(userDetails, userDetails.getAuthorities());
	}

	private boolean invalidAccessToken(DecodedJWT decodedAccessToken) {
		if (!ObjectUtils.nullSafeEquals(cognitoProps.getIssuer(), decodedAccessToken.getIssuer())) {
			return true;
		}
		if (isTokenExpired(decodedAccessToken)) {
			return true;
		}
		return false;
	}

	private boolean isTokenExpired(DecodedJWT decodedToken) {
		LocalDateTime expiredAt = decodedToken.getExpiresAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime now = LocalDateTime.now();
		return now.isAfter(expiredAt);
	}

	/**
	 * ローカルのキーID(kid)とパブリックのkidの比較
	 * @param decodedAccessToken
	 * @return
	 */
	private boolean isKidInvalid(DecodedJWT decodedAccessToken) {
		return !nullSafeEquals(cognitoProps.getKid(), decodedAccessToken.getKeyId());
	}

	/**
	 * 発行者(iss)のクレームがユーザプールと一致する
	 * @param decodedAccessToken
	 * @return
	 */
	private boolean isIssuerInvalid(DecodedJWT decodedAccessToken) {
		return !nullSafeEquals(cognitoProps.getIssuer(), decodedAccessToken.getIssuer());
	}

	private Optional<User> getUser(String username) {
		try {
			return Optional.ofNullable(userDetailsService.loadUserByUsername(username))
					.map(userDetails -> ((CustomUserDetails) userDetails).getUser());
		} catch (UsernameNotFoundException e) {
			return Optional.empty();
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AccessTokenAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}
