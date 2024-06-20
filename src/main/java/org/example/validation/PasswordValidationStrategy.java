package org.example.validation;

public interface PasswordValidationStrategy {
    boolean isValid(String password);
}
