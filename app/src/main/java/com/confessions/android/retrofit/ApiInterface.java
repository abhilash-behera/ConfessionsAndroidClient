package com.confessions.android.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Abhilash on 17-09-2017
 */

public interface ApiInterface {

    @Headers("Content-type:application/json")
    @POST("/signup")
    Call<SignUpResponse> signup(@Body SignUpRequest signUpRequest);

    @Headers("Content-type:application/json")
    @POST("/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @Headers("Content-type:application/json")
    @POST("/checkUsername")
    Call<UserCheckResponse> checkUsernameAvailability(@Body UserCheckRequest userCheckRequest);

    @Multipart
    @POST("/createPost")
    Call<CreatePostResponse> createPost(
            @Part MultipartBody.Part image,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("type") RequestBody type,
            @Part("author") RequestBody author,
            @Part("time") RequestBody time
            );

    @Headers("Content-type:application/json")
    @GET("/posts/{postType}")
    Call<PostResponse> getPosts(@Path("postType") String postType);
}