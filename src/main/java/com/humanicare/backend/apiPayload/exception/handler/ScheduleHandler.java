package com.humanicare.backend.apiPayload.exception.handler;

import com.humanicare.backend.apiPayload.code.BaseErrorCode;
import com.humanicare.backend.apiPayload.exception.GeneralException;

public class ScheduleHandler extends GeneralException {
    public ScheduleHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
