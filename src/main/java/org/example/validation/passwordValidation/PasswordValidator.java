package org.example.validation.passwordValidation;

import java.util.ArrayList;
import java.util.List;

public class PasswordValidator {
    private List<PasswordValidationStrategy> strategies = new ArrayList<>();

    public void addStrategy(PasswordValidationStrategy strategy) {
        strategies.add(strategy);
    }

    public boolean validate(String password) {
        for (PasswordValidationStrategy strategy : strategies) {
            if (!strategy.isValid(password)) {
                return false;
            }
        }
        return true;
    }
}
