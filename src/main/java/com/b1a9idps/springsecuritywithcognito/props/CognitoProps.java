package com.b1a9idps.springsecuritywithcognito.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cloud.aws.cognito")
public class CognitoProps extends AwsProps {
	private boolean enabled;

	private String userPoolId;

	private String clientId;

	private String kid;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getUserPoolId() {
		return userPoolId;
	}

	public void setUserPoolId(String userPoolId) {
		this.userPoolId = userPoolId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getKid() {
		return kid;
	}

	public void setKid(String kid) {
		this.kid = kid;
	}

	public String getIssuer() {
		return "https://cognito-idp." + getRegion().getName() + ".amazonaws.com/" + this.userPoolId;
	}
}
