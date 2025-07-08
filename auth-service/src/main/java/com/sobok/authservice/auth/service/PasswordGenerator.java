package com.sobok.authservice.auth.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String SPECIALS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String ALL = UPPERCASE + LOWERCASE + NUMBERS + SPECIALS;

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        if (length < 8 || length > 16) {
            throw new IllegalArgumentException("Password length must be between 8 and 16 characters.");
        }

        List<Character> passwordChars = new ArrayList<>();

        // 최소 한 글자씩 포함
        passwordChars.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        passwordChars.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        passwordChars.add(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        passwordChars.add(SPECIALS.charAt(random.nextInt(SPECIALS.length())));

        // 나머지 채우기
        for (int i = 4; i < length; i++) {
            passwordChars.add(ALL.charAt(random.nextInt(ALL.length())));
        }

        // 섞기
        Collections.shuffle(passwordChars);

        // 문자열로 변환
        StringBuilder password = new StringBuilder();
        for (char ch : passwordChars) {
            password.append(ch);
        }

        return password.toString();
    }
}
