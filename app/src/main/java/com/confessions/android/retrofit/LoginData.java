package com.confessions.android.retrofit;

import java.io.Serializable;

public class LoginData{
    public String username;
    public String dob;
    public String country;
    public String gender;
    public String email;
    public String secretQuestion;
    public String secretAnswer;

    public String getEmail() {
        return email;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public String getUsername() {
        return username;
    }

    public String getDob() {
        return dob;
    }

    public String getCountry() {
        return country;
    }

    public String getGender() {
        return gender;
    }
}
