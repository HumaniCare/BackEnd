package com.humanicare.backend.apiPayload.exception.handler;


import com.humanicare.backend.apiPayload.code.BaseErrorCode;
import com.humanicare.backend.apiPayload.exception.GeneralException;

public class RefreshTokenHandler extends GeneralException {

    public RefreshTokenHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
