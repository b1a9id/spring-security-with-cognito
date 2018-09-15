package com.example.springsecuritywithcognito.props;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.AwsProfileRegionProvider;
import com.amazonaws.regions.Regions;
import org.springframework.stereotype.Component;

@Component
public class AwsProps {
	private final Regions region;

	private final ProfileCredentialsProvider credentialsProvider;

	public AwsProps() {
		AwsProfileRegionProvider regionProvider = new AwsProfileRegionProvider("profile develop-cognito-user");
		this.region = Regions.fromName(regionProvider.getRegion());
		this.credentialsProvider = new ProfileCredentialsProvider("develop-cognito-user");
	}

	public Regions getRegion() {
		return region;
	}

	public ProfileCredentialsProvider getCredentialsProvider() {
		return credentialsProvider;
	}
}
