package com.confessions.android.retrofit;

import java.io.Serializable;

public class SignUpRequest implements Serializable{
    public String username;
    public String password;
    public String dob;
    public String country;
    public String gender;
    public String email;
    public String secretQuestion;
    public String secretAnswer;

    public SignUpRequest(String username, String password, String dob, String country,
                         String gender, String email, String secretQuestion, String secretAnswer) {
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.country = country;
        this.gender = gender;
        this.email = email;
        this.secretQuestion = secretQuestion;
        this.secretAnswer = secretAnswer;
    }
}
