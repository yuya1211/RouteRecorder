package com.example.routerecorder;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
 
public class SplashScreen extends Activity {
 
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	final ActionBar bar = getActionBar();
    	bar.hide();
       // bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.splash_screen);
        
        
 
        new Handler().postDelayed(new Runnable() {
 
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, FragmentTabs.class);
                
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}