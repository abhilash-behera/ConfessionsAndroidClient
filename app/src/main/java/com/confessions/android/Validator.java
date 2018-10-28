package com.confessions.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    private static String USERNAME_REGEX="^[a-z0-9_-]{3,15}$";
    private static String EMAIL_REGEX="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$";
    private static Pattern pattern;
    private static Matcher matcher;

    public static boolean validateUsername(CharSequence username){
        pattern=Pattern.compile(USERNAME_REGEX);
        matcher=pattern.matcher(username);
        return matcher.matches();
    }

    public static boolean validateEmail(CharSequence email){
        pattern=Pattern.compile(EMAIL_REGEX);
        matcher=pattern.matcher(email);
        return matcher.matches();
    }
}