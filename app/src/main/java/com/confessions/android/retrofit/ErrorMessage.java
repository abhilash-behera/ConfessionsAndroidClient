package com.confessions.android.retrofit;

import java.io.Serializable;

public class ErrorMessage implements Serializable{
    public boolean success;
    public String data;

    public boolean isSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }
}
