package com.example.hp.godgiftchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mAlready_have_accnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn = (Button) findViewById(R.id.start_reg_btn);
        mAlready_have_accnt = (Button) findViewById(R.id.main_already_have_accnt);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
                finish();
            }
        });
        mAlready_have_accnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent log_intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(log_intent);
                finish();

            }
        });
    }
}
