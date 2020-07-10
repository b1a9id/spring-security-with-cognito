package com.b1a9idps.springsecuritywithcognito.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.util.StringUtils;

public class JWTUtils {
	private JWTUtils() {}

	public static DecodedJWT decode(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return JWT.decode(value);
		} catch (JWTDecodeException e) {
			return null;
		}
	}
}
