package org.example.validation.passwordValidation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class WeakPasswordValidation implements PasswordValidationStrategy {
    private Set<String> weakPasswords;

    public WeakPasswordValidation(String filePath) {
        this.weakPasswords = loadCommonPasswords(filePath);
    }

    private Set<String> loadCommonPasswords(String filePath) {
        Set<String> passwords = new HashSet<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                passwords.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return passwords;
    }

    @Override
    public boolean isValid(String password) {
        return password != null && !weakPasswords.contains(password);
    }
}
