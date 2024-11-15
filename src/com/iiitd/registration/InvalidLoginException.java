package com.iiitd.registration;

public class InvalidLoginException extends RuntimeException {
  public InvalidLoginException(String message) {
    super(message);
  }
}
