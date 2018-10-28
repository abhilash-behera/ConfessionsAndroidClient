package com.confessions.android.retrofit;

import java.io.Serializable;

public class CreatePostResponse implements Serializable {
    public boolean success;
    public String data;

    public boolean isSuccess() {
        return success;
    }

    public String getData() {
        return data;
    }
}
