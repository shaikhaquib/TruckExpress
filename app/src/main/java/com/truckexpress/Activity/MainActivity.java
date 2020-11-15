package com.truckexpress.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.truckexpress.Activity.Add.AddCheckList;
import com.truckexpress.Activity.Add.AddTruckDetails;
import com.truckexpress.Extras.AppExecutor;
import com.truckexpress.Extras.Constants;
import com.truckexpress.R;
import com.truckexpress.Room.SessionManager;
import com.truckexpress.Room.UserDatabase;

import static com.truckexpress.Activity.SplashScreen.USERINFO;

public class MainActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    private AppBarConfiguration mAppBarConfiguration;
    Toolbar toolbar;
    UserDatabase userDatabase;
    DrawerLayout drawer;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDatabase = Room.databaseBuilder(getApplicationContext(), UserDatabase.class, Constants.DATABASE_NAME).fallbackToDestructiveMigration().build();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_logout).setOnMenuItemClickListener(this::onMenuItemClick);
        menu.findItem(R.id.nav_truck).setOnMenuItemClickListener(this::onMenuItemClick);
        menu.findItem(R.id.nav_locationList).setOnMenuItemClickListener(this::onMenuItemClick);
        menu.findItem(R.id.nav_checkList).setOnMenuItemClickListener(this::onMenuItemClick);

        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_truck, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


       // setToolbarTitle(USERINFO.getFullname());

        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                USERINFO = userDatabase.dbAccess().getUserDetail();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                drawer.closeDrawers();
                new SessionManager(MainActivity.this).setLogin(false);
                AppExecutor.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        userDatabase.dbAccess().deleteUser(USERINFO);
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                    }
                });
                break;
            case R.id.nav_truck:
                drawer.closeDrawers();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        startActivity(new Intent(getApplicationContext(), AddTruckDetails.class));
                    }
                }, 300);
                break;
            case R.id.nav_locationList:
                drawer.closeDrawers();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        startActivity(new Intent(getApplicationContext(), ActivityRoute.class));
                    }
                }, 300);
                break;
            case R.id.nav_checkList:
                drawer.closeDrawers();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        startActivity(new Intent(getApplicationContext(), AddCheckList.class));
                    }
                }, 300);
                break;
        }
        return false;
    }
}