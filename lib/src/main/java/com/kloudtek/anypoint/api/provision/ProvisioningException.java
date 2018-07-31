package com.kloudtek.anypoint.api.provision;

public class ProvisioningException extends Exception {
    public ProvisioningException() {
    }

    public ProvisioningException(String message) {
        super(message);
    }

    public ProvisioningException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProvisioningException(Throwable cause) {
        super(cause);
    }

    public ProvisioningException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
