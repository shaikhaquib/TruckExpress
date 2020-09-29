package com.truckexpress.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.truckexpress.Activity.Payment.AdavancePayments;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityPaymentBinding;

public class ActivityPayment extends AppCompatActivity implements View.OnClickListener {

    ActivityPaymentBinding activityPaymentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPaymentBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Payments");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);

        activityPaymentBinding.advancePayment.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advancePayment:
                startActivity(new Intent(getApplicationContext(), AdavancePayments.class));
                break;
        }
    }
}