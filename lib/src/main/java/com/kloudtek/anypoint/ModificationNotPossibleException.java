package com.kloudtek.anypoint;

public class ModificationNotPossibleException extends Exception {
    public ModificationNotPossibleException() {
    }

    public ModificationNotPossibleException(String message) {
        super(message);
    }

    public ModificationNotPossibleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModificationNotPossibleException(Throwable cause) {
        super(cause);
    }

    public ModificationNotPossibleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
