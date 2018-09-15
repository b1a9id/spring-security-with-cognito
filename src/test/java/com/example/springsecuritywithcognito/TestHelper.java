package com.example.springsecuritywithcognito;

import org.springframework.http.HttpHeaders;

public class TestHelper {

    public static HttpHeaders createHttpHeader(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("cache-control", "no-cache");
        httpHeaders.add("content-type", "application/json");
        httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
        return httpHeaders;
    }
}
