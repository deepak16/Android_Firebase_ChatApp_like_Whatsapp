package com.example.hp.godgiftchatapp;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private  SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        mViewPager = (ViewPager) findViewById(R.id.tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);



        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("GodGift Chat App");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){

            FirebaseAuth.getInstance().signOut();
            getbacktostart();
        }
        else if(item.getItemId() == R.id.main_account_settings_btn){

            Intent accntsetting = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(accntsetting);
            finish();
        }
        else if(item.getItemId() == R.id.main_all_users_btn){

            Intent accntsetting = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(accntsetting);
            finish();
        }



        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){

            getbacktostart();

        }
    }

    private void getbacktostart() {

        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
         finish();
    }
}
