package com.truckexpress.Activity.ui.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.truckexpress.Activity.EnquiresActivity;
import com.truckexpress.Activity.LoginActivity;
import com.truckexpress.Activity.MainActivity;
import com.truckexpress.Activity.OtpVerification;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.UserInfo;
import com.truckexpress.Network.AppController;
import com.truckexpress.R;
import com.truckexpress.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static androidx.databinding.DataBindingUtil.inflate;
import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.DashboardCount;
import static com.truckexpress.Network.API.LOGIN;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;
    private MainActivity activity;
    FragmentHomeBinding binding;
    Progress progress;
    private static final String TAG = "HomeFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        binding = inflate(inflater, R.layout.fragment_home, container, false);

        activity = (MainActivity)getActivity();
//        Log.d(TAG, "onCreateView: "+ USERINFO.getFullname());
        activity.setToolbarTitle(Constants.capitalize(USERINFO.getFullname()));
        progress = new Progress(getActivity());
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        binding.Enquiries.setOnClickListener(this);
        binding.EnquiriesValue.setOnClickListener(this);


        new DashBoard().execute();

        return binding.getRoot();
    }
    public class DashBoard extends AsyncTask<String , Void ,String> {
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
                url = new URL(DashboardCount);
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

                String s = "{\n" +
                        "   \"userid\": 5\n" +
                        "}";

                OutputStream os = conn.getOutputStream();
                os.write(s.getBytes("UTF-8"));
                os.close();

            } catch (IOException e) {
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

            if (!s.equals("unsuccessful")){
                try {
                    JSONObject object = new JSONArray(s).getJSONObject(0);
                    binding.EnquiriesValue.setText(String.valueOf(object.getInt("EnquiryCount")));
                    binding.BookingValue.setText(String.valueOf(object.getInt("BookingCount")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Alert(getActivity(),"Some thing went wrong..");
            }

        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.Enquiries:
                startActivity(new Intent(getActivity(), EnquiresActivity.class));
                break;
            case R.id.EnquiriesValue:
                startActivity(new Intent(getActivity(), EnquiresActivity.class));
                break;
        }

    }
}