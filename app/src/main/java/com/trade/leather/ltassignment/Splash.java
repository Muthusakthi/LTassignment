package com.trade.leather.ltassignment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mipstech i5 2 on 20-Mar-18.
 */

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(5 * 1000);

                    Intent i = new Intent(Splash.this,MainActivity.class);
                    startActivity(i);

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }

}
