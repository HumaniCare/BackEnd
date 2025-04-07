package com.humanicare.backend.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    FIRST("ROLE_FIRST"), SAVED("ROLE_SAVED");

    private final String key;
}
