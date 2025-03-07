package com.humanicare.backend.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";
    public static final String ACCESS_TOKEN_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_REPLACEMENT = "";

    public static final String BASE_TIME_ZONE = "UTC";
    public static final String NEW_TIME_ZONE = "Asia/Seoul";
}
