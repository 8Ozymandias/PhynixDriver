package com.thephynix.www.phynix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
    }

    public void RegisterScreen(View view){
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(i);
    }

    public void Login_Dash(View view){
        Intent i = new Intent(LoginActivity.this, DashboardActivity.class);
        LoginActivity.this.startActivity(i);
    }
}
