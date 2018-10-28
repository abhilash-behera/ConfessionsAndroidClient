package com.confessions.android.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.confessions.android.R;
import com.confessions.android.Utils;

public class SplashScreenActivity extends AppCompatActivity {
    private ImageView imgLogo;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imgLogo=findViewById(R.id.imgLogo);
        sharedPreferences=getSharedPreferences(Utils.USER_SHARED_PREF,MODE_PRIVATE);

        AlphaAnimation alphaAnimation=new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(2000);
        imgLogo.startAnimation(alphaAnimation);

        imgLogo.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean showGuide=sharedPreferences.getBoolean(Utils.KEY_SHOW_GUIDE,true);
                Intent intent=null;
                if(showGuide){
                    intent=new Intent(SplashScreenActivity.this,GuideActivity.class);
                }else{
                    SharedPreferences sharedPreferences=getSharedPreferences(Utils.USER_SHARED_PREF,MODE_PRIVATE);
                    if(sharedPreferences.getString(Utils.KEY_USER_NAME,null)==null){
                        intent=new Intent(SplashScreenActivity.this,LoginActivity.class);
                    }else{
                        intent=new Intent(SplashScreenActivity.this,MainActivity.class);
                    }
                }
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
