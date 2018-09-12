package com.example.springsecuritywithcognito.config;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.example.springsecuritywithcognito.props.AwsProps;
import com.example.springsecuritywithcognito.props.CognitoProps;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CognitoClientConfig {
	@ConfigurationProperties(prefix = "cloud.aws.cognito")
	@Bean(name = "userManagementClientProps")
	public CognitoProps userManagementClientProps() {
		return new CognitoProps();
	}

	@Bean
	public AWSCognitoIdentityProvider awsCognitoIdentityProvider(AwsProps awsProps) {
		return AWSCognitoIdentityProviderClientBuilder.standard()
				.withRegion(awsProps.getRegion())
				.withCredentials(awsProps.getCredentialsProvider())
				.build();
	}
}
