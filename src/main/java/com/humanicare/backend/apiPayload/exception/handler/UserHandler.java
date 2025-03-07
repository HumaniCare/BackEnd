package com.humanicare.backend.apiPayload.exception.handler;

import com.humanicare.backend.apiPayload.code.BaseErrorCode;
import com.humanicare.backend.apiPayload.exception.GeneralException;

public class UserHandler extends GeneralException {

    public UserHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
