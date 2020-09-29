package com.truckexpress.Activity.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.truckexpress.Activity.ActivityAdd;
import com.truckexpress.Activity.ActivityPayment;
import com.truckexpress.Activity.ActivityRoute;
import com.truckexpress.Activity.BookingActivity;
import com.truckexpress.Activity.EnquiresActivity;
import com.truckexpress.Activity.MainActivity;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.Progress;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static androidx.databinding.DataBindingUtil.inflate;
import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.DashboardCount;


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
        binding.Booking.setOnClickListener(this);
        binding.BookingValue.setOnClickListener(this);
        binding.routes.setOnClickListener(this);
        binding.add.setOnClickListener(this);
        binding.Payments.setOnClickListener(this);


        new DashBoard().execute();

        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.Enquiries:
                startActivity(new Intent(getActivity(), EnquiresActivity.class));
                break;
            case R.id.EnquiriesValue:
                startActivity(new Intent(getActivity(), EnquiresActivity.class));
                break;
            case R.id.Booking:
                startActivity(new Intent(getActivity(), BookingActivity.class));
                break;
            case R.id.BookingValue:
                startActivity(new Intent(getActivity(), BookingActivity.class));
                break;
            case R.id.routes:
                startActivity(new Intent(getActivity(), ActivityRoute.class));
                break;
            case R.id.add:
                startActivity(new Intent(getActivity(), ActivityAdd.class));
                break;
            case R.id.Payments:
                startActivity(new Intent(getActivity(), ActivityPayment.class));
                break;
        }

    }

    public class DashBoard extends AsyncTask<String, Void, String> {
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
                os.write(s.getBytes(StandardCharsets.UTF_8));
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
}