package com.confessions.android.retrofit;

import java.io.Serializable;

public class LoginResponse implements Serializable{
    public boolean success=false;
    public LoginData data=null;

    public boolean isSuccess() {
        return success;
    }

    public LoginData getData() {
        return data;
    }
}
