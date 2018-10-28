package com.confessions.android.retrofit;

import java.io.Serializable;

public class SignUpResponse implements Serializable{
    public boolean success=false;
    public String data="";

    public boolean isSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }
}
