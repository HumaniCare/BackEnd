package com.humanicare.backend.apiPayload.exception.handler;

import com.humanicare.backend.apiPayload.code.BaseErrorCode;
import com.humanicare.backend.apiPayload.exception.GeneralException;

public class BasicScheduleHandler extends GeneralException {

    public BasicScheduleHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
