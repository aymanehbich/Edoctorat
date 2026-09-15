package com.edoc.exception;

public class exceptionEmail {

  public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException() {
            super("Email already exists");
        }
    }

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException() {
            super("Invalid token");
        }
    }

    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException() {
            super("Token has expired");
        }
    }
}
