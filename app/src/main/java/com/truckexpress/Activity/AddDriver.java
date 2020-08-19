package com.truckexpress.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.truckexpress.Models.ArrayItem;
import com.truckexpress.Models.ModelStateCity;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddDriverBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddDriver extends AppCompatActivity {

    ActionBar actionBar;
    private JSONArray jsonCountryArray;
    private Spinner countrySpinner;
    private Spinner stateSpinner;
    private Spinner citySpinner;
    private static final String TAG = "AddDriver";
    List<ArrayItem> arrayItems = new ArrayList<>();
    ActivityAddDriverBinding activityAddDriverBinding;
    List<String> stateList = new ArrayList<>();
    List<String> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddDriverBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_driver);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Add Driver");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Gson gson = new Gson();
        ModelStateCity modelStateCity = gson.fromJson(loadJSONFromAsset().toString(),ModelStateCity.class);
        arrayItems.addAll(modelStateCity.getArray());




    }

    private void displayCities(String[] array) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddDriver.this);
        builder.setCancelable(true);
        builder.setTitle("Select City");
        builder.setItems(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String state = array[which];
                for (int i = 0; i < arrayItems.size(); i++) {
                    ArrayItem arrayItem = arrayItems.get(i);
                    if (state.equals(arrayItem.getState()))
                        cityList.add(arrayItem.getName());
                }

            }
        });
        builder.show();
    }

    private void displayState(String[] array) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddDriver.this);
        builder.setCancelable(true);
        builder.setTitle("Select State");
        builder.setItems(array, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String state = array[which].trim();
                cityList.clear();

                for (int i = 0; i < arrayItems.size(); i++) {
                    ArrayItem arrayItem = arrayItems.get(i);
                    if (state.equals(arrayItem.getState().trim()))
                      cityList.add(arrayItem.getName());
                }

            }
        });
        builder.show();
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


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}