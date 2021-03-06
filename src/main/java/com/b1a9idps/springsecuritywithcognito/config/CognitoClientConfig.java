package com.b1a9idps.springsecuritywithcognito.config;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.b1a9idps.springsecuritywithcognito.props.AwsProps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoClientConfig {
	@Bean
	public AWSCognitoIdentityProvider awsCognitoIdentityProvider(AwsProps awsProps) {
		return AWSCognitoIdentityProviderClientBuilder.standard()
				.withRegion(awsProps.getRegion())
				.withCredentials(awsProps.getCredentialsProvider())
				.build();
	}
}
