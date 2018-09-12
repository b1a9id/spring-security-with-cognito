package com.example.springsecuritywithcognito.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.springsecuritywithcognito.props.CognitoProps;
import com.example.springsecuritywithcognito.security.dto.AuthenticatedUserDetails;
import com.example.springsecuritywithcognito.utils.JWTUtils;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;

import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;
import static org.springframework.util.ObjectUtils.nullSafeEquals;

public class UserAuthenticatedVoter implements AccessDecisionVoter<FilterInvocation> {
	private final CognitoProps cognitoProps;
	private final UserDetailsService userDetailsService;
	private final AuthenticationTrustResolver authenticationTrustResolver;

	public UserAuthenticatedVoter(CognitoProps cognitoProps, UserDetailsService userDetailsService, AuthenticationTrustResolver authenticationTrustResolver) {
		this.cognitoProps = cognitoProps;
		this.userDetailsService = userDetailsService;
		this.authenticationTrustResolver = authenticationTrustResolver;
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	@Override
	public int vote(Authentication authentication, FilterInvocation filterInvocation, Collection<ConfigAttribute> attributes) {
		if (authentication == null || filterInvocation == null || attributes == null) {
			return ACCESS_DENIED;
		}

		if (authenticationTrustResolver.isAnonymous(authentication)) {
			return ACCESS_ABSTAIN;
		}

		String accessToken = ((AuthenticatedUserDetails) authentication.getPrincipal()).getAccessToken();
		if (accessToken == null) {
			return ACCESS_DENIED;
		}

		DecodedJWT decodedAccessToken = JWTUtils.decode(accessToken);
		if (decodedAccessToken == null) {
			return ACCESS_DENIED;
		}

		if (isKidInvalid(decodedAccessToken)) {
			return ACCESS_DENIED;
		}

		if (isIssuerInvalid(decodedAccessToken)) {
			return ACCESS_DENIED;
		}

		String username = decodedAccessToken.getClaim(SPRING_SECURITY_FORM_USERNAME_KEY).asString();
		if (isUserNotExist(username)) {
			return ACCESS_DENIED;
		}

		return ACCESS_GRANTED;
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

	private boolean isUserNotExist(String username) {
		try {
			return userDetailsService.loadUserByUsername(username) == null;
		} catch (UsernameNotFoundException e) {
			return true;
		}
	}
}
