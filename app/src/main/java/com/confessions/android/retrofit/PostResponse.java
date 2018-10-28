package com.confessions.android.retrofit;

import java.io.Serializable;
import java.util.List;

public class PostResponse implements Serializable {
    public boolean success;
    public List<Post> data;

    public boolean isSuccess() {
        return success;
    }

    public List<Post> getData() {
        return data;
    }
}
