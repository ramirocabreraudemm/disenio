package org.example.validation;

public class ComplexityValidation implements PasswordValidationStrategy {

    @Override
    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[^a-zA-Z0-9].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}
