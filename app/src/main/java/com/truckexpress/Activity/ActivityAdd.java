package com.truckexpress.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.truckexpress.Activity.Add.AddBooking;
import com.truckexpress.Activity.Add.AddCheckList;
import com.truckexpress.Activity.Add.AddCompany;
import com.truckexpress.Activity.Add.AddDriver;
import com.truckexpress.Activity.Add.AddExpense;
import com.truckexpress.Activity.Add.AddGoodType;
import com.truckexpress.Activity.Add.AddMoterOwner;
import com.truckexpress.Activity.Add.AddTruckDetails;
import com.truckexpress.Activity.Map.MapsActivity;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddBinding;

public class ActivityAdd extends AppCompatActivity implements View.OnClickListener {

    ActivityAddBinding activityAddBinding;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddBinding = DataBindingUtil.setContentView(this,R.layout.activity_add);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        activityAddBinding.addDriver.setOnClickListener(this);
        activityAddBinding.addTruckDetails.setOnClickListener(this);
        activityAddBinding.addGoodsDetails.setOnClickListener(this);
        activityAddBinding.addMoterOwner.setOnClickListener(this);
        activityAddBinding.addExpense.setOnClickListener(this);
        activityAddBinding.addCheckList.setOnClickListener(this);
        activityAddBinding.addCompany.setOnClickListener(this);
        activityAddBinding.addRoute.setOnClickListener(this);
        activityAddBinding.addBooking.setOnClickListener(this);




    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.addDriver:
                startActivity(new Intent(getApplicationContext(), AddDriver.class));
                break;
            case R.id.addTruckDetails:
                startActivity(new Intent(getApplicationContext(), AddTruckDetails.class));
                break;
            case R.id.addGoodsDetails:
                startActivity(new Intent(getApplicationContext(), AddGoodType.class));
                break;
            case R.id.addMoterOwner:
                startActivity(new Intent(getApplicationContext(), AddMoterOwner.class));
                break;
            case R.id.addExpense:
                startActivity(new Intent(getApplicationContext(), AddExpense.class));
                break;
            case R.id.addCheckList:
                startActivity(new Intent(getApplicationContext(), AddCheckList.class));
                break;
            case R.id.addCompany:
                startActivity(new Intent(getApplicationContext(), AddCompany.class));
                break;
            case R.id.addRoute:
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                break;
            case R.id.addBooking:
                startActivity(new Intent(getApplicationContext(), AddBooking.class));
                break;
        }
    }
}