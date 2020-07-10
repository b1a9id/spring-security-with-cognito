package com.b1a9idps.springsecuritywithcognito.utils;

import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

	private CookieUtils() {}

	public static void addCookie(
			HttpServletRequest request,
			HttpServletResponse response,
			String key,
			String value) {
		Cookie cookie = WebUtils.getCookie(request, key);
		if (cookie == null) {
			cookie = new Cookie(key, value);
		} else {
			cookie.setValue(value);
		}
		cookie.setPath("/");
		cookie.setMaxAge(30);
		if ("https".equals(request.getScheme())) {
			cookie.setSecure(true);
		}
		response.addCookie(cookie);
	}
}
