package com.ozymandias.phynixdriver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


    }

    public void Login(View view){
        Intent i = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(i);
    }

    public void Register(View view){
        Intent i = new Intent(MainActivity.this, RegisterActivity.class);
        MainActivity.this.startActivity(i);
    }
}
