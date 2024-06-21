package org.example.validation.sessionValidation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SessionValidation {
    private final int MAX_ATTEMPTS = 5;
    private final long LOCK_TIME_DURATION = 300000; // 5 minutos en milisegundos
    private Map<String, LoginAttempt> attempts = new HashMap<>();

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }

    public void loginFailed(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null) {
            attempt = new LoginAttempt(1, new Date());
        } else {
            System.out.println("hola");
            System.out.println(attempt.getAttempts());
            attempt.setAttempts(attempt.getAttempts() + 1);
            attempt.setLastAttempt(new Date());
        }
        attempts.put(key, attempt);
    }

    public boolean isBlocked(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null) {
            return false;
        }
        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            long diff = new Date().getTime() - attempt.getLastAttempt().getTime();
            if (diff < LOCK_TIME_DURATION) {
                return true;
            } else {
                attempts.remove(key);
            }
        }
        return false;
    }

    public long getRemainingLockTime(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null) {
            return 0;
        }
        long diff = new Date().getTime() - attempt.getLastAttempt().getTime();
        return Math.max(LOCK_TIME_DURATION - diff, 0);
    }

    public int getRemainingAttempts(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null) {
            return MAX_ATTEMPTS;
        }
        return MAX_ATTEMPTS - attempt.getAttempts();
    }

    private static class LoginAttempt {
        private int attempts;
        private Date lastAttempt;

        public LoginAttempt(int attempts, Date lastAttempt) {
            this.attempts = attempts;
            this.lastAttempt = lastAttempt;
        }

        public int getAttempts() {
            return attempts;
        }

        public void setAttempts(int attempts) {
            this.attempts = attempts;
        }

        public Date getLastAttempt() {
            return lastAttempt;
        }

        public void setLastAttempt(Date lastAttempt) {
            this.lastAttempt = lastAttempt;
        }
    }
}
