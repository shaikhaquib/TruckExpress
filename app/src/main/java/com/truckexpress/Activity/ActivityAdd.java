package com.truckexpress.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.gson.Gson;
import com.truckexpress.Models.ArrayItem;
import com.truckexpress.Models.ModelStateCity;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        switch (id){
            case R.id.addDriver:
                startActivity(new Intent(getApplicationContext(),AddDriver.class));
                break;
        }
    }
}