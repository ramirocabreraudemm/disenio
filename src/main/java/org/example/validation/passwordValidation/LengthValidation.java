package org.example.validation.passwordValidation;

public class LengthValidation implements PasswordValidationStrategy {
    private int minLength;

    public LengthValidation(int minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean isValid(String password) {
        return password != null && password.length() >= minLength;
    }
}
