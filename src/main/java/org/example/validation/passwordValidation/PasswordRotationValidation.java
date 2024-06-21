package org.example.validation.passwordValidation;

import org.example.model.User;
import org.example.service.UserService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PasswordRotationValidation implements PasswordValidationStrategy {
    private final long rotationPeriodDays;
    private final UserService userService;

    public PasswordRotationValidation(long rotationPeriodDays, UserService userService) {
        this.rotationPeriodDays = rotationPeriodDays;
        this.userService = userService;
    }

    public boolean isValid(String password) {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Date lastChanged = user.getPasswordChangedAt();
        if (lastChanged == null) {
            return true;
        }

        long diffInMillies = Math.abs(new Date().getTime() - lastChanged.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff >= rotationPeriodDays;
    }
}
