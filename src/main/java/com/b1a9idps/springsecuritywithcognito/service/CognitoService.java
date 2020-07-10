package com.b1a9idps.springsecuritywithcognito.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.b1a9idps.springsecuritywithcognito.exception.FailedAuthenticationException;
import com.b1a9idps.springsecuritywithcognito.props.CognitoProps;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.amazonaws.services.cognitoidp.model.ChallengeNameType.NEW_PASSWORD_REQUIRED;

@Service
public class CognitoService {

	private final CognitoProps cognitoProps;

	private final AWSCognitoIdentityProvider cognitoIdentityProvider;

	public CognitoService(CognitoProps cognitoProps, AWSCognitoIdentityProvider cognitoIdentityProvider) {
		this.cognitoProps = cognitoProps;
		this.cognitoIdentityProvider = cognitoIdentityProvider;
	}

	public Optional<AdminGetUserResult> adminGetUser(String username) {
		AdminGetUserRequest request = new AdminGetUserRequest();
		request.withUserPoolId(cognitoProps.getUserPoolId())
				.withUsername(username);
		try {
			return Optional.ofNullable(cognitoIdentityProvider.adminGetUser(request));
		} catch (UserNotFoundException e) {
			return Optional.empty();
		}
	}

	public Optional<AdminInitiateAuthResult> adminInitiateAuth(String username, String password) {
		AdminInitiateAuthRequest adminInitiateAuthRequest = new AdminInitiateAuthRequest();
		adminInitiateAuthRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
				.withUserPoolId(cognitoProps.getUserPoolId())
				.withClientId(cognitoProps.getClientId())
				.addAuthParametersEntry("USERNAME", username)
				.addAuthParametersEntry("PASSWORD", password);
		return adminInitiateAuthResult(adminInitiateAuthRequest);
	}

	private Optional<AdminInitiateAuthResult> adminInitiateAuthResult(AdminInitiateAuthRequest request) {
		try {
			return Optional.of(cognitoIdentityProvider.adminInitiateAuth(request));
		} catch (NotAuthorizedException e) {
			throw new FailedAuthenticationException("authenticate failed.", e);
		} catch (UserNotFoundException e) {
			String username = request.getAuthParameters().get("USERNAME");
			throw new com.b1a9idps.springsecuritywithcognito.exception.UserNotFoundException("username " + username + " not found.", e);
		}
	}

	public Optional<AdminRespondToAuthChallengeResult> adminRespondToAuthChallenge(
			String username, String newPassword, String session) {
		AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
		request.withChallengeName(NEW_PASSWORD_REQUIRED)
				.withUserPoolId(cognitoProps.getUserPoolId())
				.withClientId(cognitoProps.getClientId())
				.withSession(session)
				.addChallengeResponsesEntry("USERNAME", username)
				.addChallengeResponsesEntry("NEW_PASSWORD", newPassword);

		try {
			return Optional.of(cognitoIdentityProvider.adminRespondToAuthChallenge(request));
		} catch (UserNotFoundException e) {
			throw new com.b1a9idps.springsecuritywithcognito.exception.UserNotFoundException("user not found.", e);
		} catch (com.amazonaws.services.cognitoidp.model.InvalidPasswordException e) {
			throw new com.b1a9idps.springsecuritywithcognito.exception.InvalidPasswordException("invalid password.", e);
		}
	}
}
