package com.kloudtek.anypoint.api;

import com.kloudtek.util.UserDisplayableException;

public class RequestAPIAccessException extends UserDisplayableException {
    public RequestAPIAccessException() {
    }

    public RequestAPIAccessException(String message) {
        super(message);
    }

    public RequestAPIAccessException(String errorTitle, String message) {
        super(errorTitle, message);
    }

    public RequestAPIAccessException(String errorTitle, String message, Throwable cause, String errorCode) {
        super(errorTitle, message, cause, errorCode);
    }

    public RequestAPIAccessException(Throwable cause, String errorCode) {
        super(cause, errorCode);
    }

    public RequestAPIAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
