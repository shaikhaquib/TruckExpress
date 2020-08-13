package com.truckexpress.Activity;

import android.content.Intent;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.airbnb.lottie.L;
import com.daimajia.androidanimations.library.Techniques;
import com.google.android.material.card.MaterialCardView;
import com.truckexpress.Extras.AppExecutor;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Models.UserInfo;
import com.truckexpress.R;
import com.truckexpress.Room.SessionManager;
import com.truckexpress.Room.UserDatabase;
import com.viksaa.sssplash.lib.activity.AwesomeSplash;
import com.viksaa.sssplash.lib.model.ConfigSplash;

import io.codetail.animation.ViewAnimationUtils;

public class SplashScreen extends AwesomeSplash {
    public static UserInfo USERINFO;
    UserDatabase userDatabase;
    SessionManager sessionManager;
    public void initSplash(ConfigSplash configSplash) {
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.splash));
        configSplash.setBackgroundColor(R.color.strokeColor);
        configSplash.setAnimCircularRevealDuration(ViewAnimationUtils.SCALE_UP_DURATION);
        configSplash.setRevealFlagX(4);
        configSplash.setRevealFlagY(2);
        configSplash.setLogoSplash(R.drawable.ic_truck_final);
        configSplash.setAnimLogoSplashDuration(ViewAnimationUtils.SCALE_UP_DURATION);
        configSplash.setAnimLogoSplashTechnique(Techniques.FadeInLeft);
        configSplash.setTitleSplash("");
        configSplash.setAnimTitleDuration(0);



    }

    public void animationsFinished() {
        sessionManager = new SessionManager(this);
        userDatabase = Room.databaseBuilder(getApplicationContext(), UserDatabase.class , Constants.DATABASE_NAME).fallbackToDestructiveMigration().build();
        if (sessionManager.isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    }
}