package com.truckexpress.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.truckexpress.Activity.BookingActivity;
import com.truckexpress.Adapter.Rv_BookingInCompletedAdapt;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelCurrentBooking;
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

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.TripCompleted;

public class Fragment_Trip_Completed extends Fragment {
    private static final String TAG = "TripInCompleted";
    public List<ModelCurrentBooking> TripInCompleted = new ArrayList<>();
    RecyclerView rvLOT;
    Rv_BookingInCompletedAdapt rv_currentBookingsAdapt;
    Progress progress;
    BookingActivity mainActivity;
    SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trpin_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvLOT = view.findViewById(R.id.rvLOT);
        mainActivity = (BookingActivity) getActivity();
        rvLOT.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvLOT.hasFixedSize();
        rv_currentBookingsAdapt = new Rv_BookingInCompletedAdapt(getActivity(), TripInCompleted);
        rvLOT.setAdapter(rv_currentBookingsAdapt);
        progress = new Progress(getActivity());
        new BookingTripComplited().execute();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                rv_currentBookingsAdapt.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                rv_currentBookingsAdapt.getFilter().filter(query);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public class BookingTripComplited extends AsyncTask<String, Void, String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(TripCompleted);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
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
                    return (result.toString());


                } else {

                    return ("unsuccessful");
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
                TripInCompleted.clear();
                rv_currentBookingsAdapt.notifyDataSetChanged();

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
                                ModelCurrentBooking modelLOT = gson.fromJson(object.toString(), ModelCurrentBooking.class);
                                TripInCompleted.add(modelLOT);
                                rv_currentBookingsAdapt.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (json instanceof JSONObject) {
                        JSONObject object = new JSONObject(s);
                        Gson gson = new Gson();
                        ModelCurrentBooking modelLOT = gson.fromJson(object.toString(), ModelCurrentBooking.class);
                        TripInCompleted.add(modelLOT);
                        rv_currentBookingsAdapt.notifyDataSetChanged();

                    } else {
                        Alert(getActivity(), s);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Alert(getActivity(), "Some thing went wrong..");
            }

        }
    }


}