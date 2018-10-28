package com.confessions.android.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.confessions.android.R;
import com.confessions.android.Utils;
import com.confessions.android.retrofit.ApiClient;
import com.confessions.android.retrofit.ErrorMessage;
import com.confessions.android.retrofit.LoginRequest;
import com.confessions.android.retrofit.LoginResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText txtUsername;
    private TextInputEditText txtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword;
    private TextView txtSignUp;
    private TextInputLayout txtInpUsername;
    private TextInputLayout txtInpPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        initializeViews();
        attachListeners();
    }

    private void initializeViews() {
        txtUsername=findViewById(R.id.txtUsername);
        txtPassword=findViewById(R.id.txtPassword);
        btnLogin=findViewById(R.id.btnLogin);
        txtForgotPassword=findViewById(R.id.txtForgotPassword);
        txtSignUp=findViewById(R.id.txtSignUp);
        txtInpUsername=findViewById(R.id.txtInpUsername);
        txtInpPassword=findViewById(R.id.txtInpPassword);

        btnLogin.requestFocus();
    }

    private void attachListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtUsername.getText().toString().length()>2){
                    if(txtPassword.getText().toString().length()>5){
                        login(txtUsername.getText().toString(),txtPassword.getText().toString());
                    }else{
                        txtInpPassword.setError("Please check your password");
                    }
                }else{
                    txtInpUsername.setError("Please enter valid username");
                }
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String username, String password) {
        final ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        btnLogin.setEnabled(false);

        ApiClient.getClient().login(new LoginRequest(username,password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call,final Response<LoginResponse> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().isSuccess()){
                        Snackbar.make(btnLogin,"Welcome "+response.body().getData().getUsername(),Snackbar.LENGTH_LONG).show();
                        SharedPreferences sharedPreferences=getSharedPreferences(Utils.USER_SHARED_PREF,MODE_PRIVATE);
                        sharedPreferences.edit()
                                .putString(Utils.KEY_USER_NAME,response.body().getData().getUsername())
                                .putString(Utils.KEY_USER_EMAIL,response.body().getData().getEmail())
                                .putString(Utils.KEY_USER_DOB,response.body().getData().getDob())
                                .putString(Utils.KEY_USER_GENDER,response.body().getData().getGender())
                                .putString(Utils.KEY_USER_COUNTRY,response.body().getData().getCountry())
                                .apply();
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        },1000);

                    }else{
                        btnLogin.setEnabled(true);
                        Snackbar.make(btnLogin,"Please check your credentials",Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    Snackbar.make(btnLogin,"Please check your credentials",Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                btnLogin.setEnabled(true);
                Snackbar.make(btnLogin,"Please check your credentials and try again.",Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
