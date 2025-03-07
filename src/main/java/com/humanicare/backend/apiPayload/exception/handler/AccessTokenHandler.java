package com.humanicare.backend.apiPayload.exception.handler;


import com.humanicare.backend.apiPayload.code.BaseErrorCode;
import com.humanicare.backend.apiPayload.exception.GeneralException;

public class AccessTokenHandler extends GeneralException {

    public AccessTokenHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
