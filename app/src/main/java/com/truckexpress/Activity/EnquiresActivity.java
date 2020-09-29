package com.truckexpress.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.truckexpress.Adapter.ViewPagerAdapter;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Fragments.Fragment_ADHOC;
import com.truckexpress.Fragments.Fragment_Contract;
import com.truckexpress.Fragments.Fragment_LOT;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.BOOKINGLIST;

public class EnquiresActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    Fragment_ADHOC fragment_adHoc = new Fragment_ADHOC();
    Fragment_LOT fragment_lot = new Fragment_LOT();
    Fragment_Contract fragment_contract = new Fragment_Contract();
    ActionBar actionBar;
    public List<ModelLOT> modelLOTS = new ArrayList<>();
    public List<ModelLOT> modelADHOC = new ArrayList<>();
    private static final String TAG = "EnquiresActivity";
    Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquires);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Enquiries");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);
        setupViewPager(viewPager);
        progress = new Progress(this);
        tabLayout.setupWithViewPager(viewPager);
        new BookingListTask().execute();
    }
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(fragment_adHoc, "ADHOC");
        adapter.addFragment(fragment_lot, "LOT");
        //    adapter.addFragment(fragment_contract, "Contract");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

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


    public class BookingListTask extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(BOOKINGLIST);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                os.write(new JSONObject().put("userid", USERINFO.getId()).toString().getBytes(StandardCharsets.UTF_8));
                os.close();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());


                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();

            if (!s.equals("unsuccessful")) {


                Log.d(TAG, "onResponse: " + s);

                Object json = null;
                try {
                    json = new JSONTokener(s).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(s);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Gson gson = new Gson();
                                ModelLOT modelLOT = gson.fromJson(object.toString(), ModelLOT.class);
                                String type = modelLOT.getBookingtype();
                                if (type.equals("1")) {
                                    modelLOTS.add(modelLOT);
                                //    rvLOT.getAdapter().notifyDataSetChanged();
                                }else if (type.equals("0")){
                                    modelADHOC.add(modelLOT);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else   if (json instanceof JSONObject){
                        JSONObject object = new JSONObject(s);
                        Gson gson = new Gson();
                        ModelLOT modelLOT = gson.fromJson(object.toString(), ModelLOT.class);

                        if (modelLOT.getBookingtype().equals("1")) {
                            modelLOTS.add(modelLOT);
                          //  rvLOT.getAdapter().notifyDataSetChanged();
                        }else if (modelLOT.getBookingtype().equals("0")){
                            modelADHOC.add(modelLOT);
                        }
                    }else {
                        Alert(EnquiresActivity.this, s);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("BOOKINGDATA");
                sendBroadcast(intent);

            } else {
                Alert(EnquiresActivity.this, "Some thing went wrong..");
            }

        }
    }


}