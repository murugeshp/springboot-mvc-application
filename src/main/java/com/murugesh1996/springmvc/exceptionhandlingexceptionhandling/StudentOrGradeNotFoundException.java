package com.murugesh1996.springmvc.exceptionhandlingexceptionhandling;

public class StudentOrGradeNotFoundException extends RuntimeException{
    public StudentOrGradeNotFoundException(String message) {
        super(message);
    }

    public StudentOrGradeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentOrGradeNotFoundException(Throwable cause) {
        super(cause);
    }
}
