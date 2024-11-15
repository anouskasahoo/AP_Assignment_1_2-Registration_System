package com.iiitd.registration;

public class DropDeadlinePassedException extends RuntimeException {
    public DropDeadlinePassedException(String message) {
        super(message);
    }
}
