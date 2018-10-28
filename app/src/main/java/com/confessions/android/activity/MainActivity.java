package com.confessions.android.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.confessions.android.R;
import com.confessions.android.Utils;
import com.confessions.android.fragment.ConfessionsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FloatingActionButton fabCreateContent;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeViews();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_confessions);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_confessions));

        fabCreateContent=findViewById(R.id.fabCreateContent);
        fabCreateContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,CreatePostActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                SharedPreferences sharedPreferences=getSharedPreferences(Utils.USER_SHARED_PREF,MODE_PRIVATE);
                sharedPreferences.edit()
                        .remove(Utils.KEY_USER_NAME)
                        .remove(Utils.KEY_USER_EMAIL)
                        .remove(Utils.KEY_USER_DOB)
                        .remove(Utils.KEY_USER_GENDER)
                        .remove(Utils.KEY_USER_COUNTRY)
                        .apply();
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_confessions:
                Fragment fragment=getSupportFragmentManager().findFragmentByTag("ConfessionsFragment");
                if(fragment==null){
                    if(!Utils.isNetworkAvailable(MainActivity.this)){
                      Snackbar.make(fabCreateContent,"Check your internet connection!",Snackbar.LENGTH_LONG).show();
                    } else {
                        getSupportActionBar().setSubtitle("Confessions");
                        ConfessionsFragment confessionsFragment=new ConfessionsFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainer,confessionsFragment,"ConfessionsFragment").commit();
                    }
                }
                break;
            case R.id.nav_technology:
                break;
            case R.id.nav_politics:
                break;
            case R.id.nav_sex_relationships:
                break;
            case R.id.nav_health_fitness:
                break;
            case R.id.nav_rate_us:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_about_us:
                break;
            default:
                Toast.makeText(MainActivity.this, "Invalid Option", Toast.LENGTH_SHORT).show();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
