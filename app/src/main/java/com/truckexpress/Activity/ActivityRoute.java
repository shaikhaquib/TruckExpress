package com.truckexpress.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.truckexpress.Adapter.RV_ADHOCAdapter;
import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.Models.ModelRoute;
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
import java.util.ArrayList;
import java.util.List;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.BOOKINGLIST;
import static com.truckexpress.Network.API.RouteList;
import static com.truckexpress.Network.API.SaveRoutes;

public class ActivityRoute extends AppCompatActivity {

    RecyclerView rvRoute;
    List<ModelRoute> modelRoutes = new ArrayList<>();
    private static final String TAG = "ActivityRoute";
    Progress progress;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Routes");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);

        rvRoute = findViewById(R.id.rvRoute);
        progress = new Progress(this);
        rvRoute.setLayoutManager(new LinearLayoutManager(this));
        rvRoute.hasFixedSize();
        rvRoute.addItemDecoration(new MyItemDecoration());

        findViewById(R.id.addDestination).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;

                dialog  = new Dialog(ActivityRoute.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //...set cancelable false so that it's never get hidden
                dialog.setCancelable(false);
                //...that's the layout i told you will inflate later
                dialog.setContentView(R.layout.add_destination);
                // final android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                TextInputEditText destination = dialog.findViewById(R.id.destination);
                TextInputEditText source = dialog.findViewById(R.id.source);

                dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.findViewById(R.id.add_route).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (destination.getText().toString().isEmpty()){
                            Alert(ActivityRoute.this,"Please fill all field");
                        }else if (source.getText().toString().isEmpty()){
                            Alert(ActivityRoute.this,"Please fill all field");
                        }else {
                            new AddRoute(dialog).execute(destination.getText().toString(),source.getText().toString());
                        }
                    }
                });
            }
        });

        rvRoute.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_route,parent,false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                ModelRoute modelRoute = modelRoutes.get(position);
                viewHolder.source.setText(modelRoute.getSource());
                viewHolder.destination.setText(modelRoute.getDestination());
            }

            @Override
            public int getItemCount() {
                return modelRoutes.size();
            }
            class ViewHolder extends RecyclerView.ViewHolder {
                TextView source,destination;
                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    source =  itemView.findViewById(R.id.Source);
                    destination =  itemView.findViewById(R.id.destination);
                }
            }
        });
        new GetRoute().execute();

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


    public class GetRoute extends AsyncTask<String , Void ,String> {
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
                url = new URL(RouteList);
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
                os.write(new JSONObject().put("userid",USERINFO.getId()).toString().getBytes("UTF-8"));
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


                Log.d(TAG, "onResponse: " + s.toString());

                Object json = null;
                try {
                    json = new JSONTokener(s).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(s);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Gson gson = new Gson();
                                ModelRoute modelRoute = gson.fromJson(object.toString(), ModelRoute.class);
                                modelRoutes.add(modelRoute);
                                rvRoute.getAdapter().notifyDataSetChanged();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }else   if (json instanceof JSONObject){
                        JSONObject object = new JSONObject(s);
                        Gson gson = new Gson();
                        ModelRoute modelRoute = gson.fromJson(object.toString(), ModelRoute.class);
                        modelRoutes.add(modelRoute);
                        rvRoute.getAdapter().notifyDataSetChanged();

                    }else {
                        Alert(ActivityRoute.this, s);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent("BOOKINGDATA");
                sendBroadcast(intent);

            } else {
                Alert(ActivityRoute.this, "Some thing went wrong..");
            }

        }
    }
    public class AddRoute extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        String source;
        String destination;
        Dialog dialog;

        public AddRoute(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(SaveRoutes);
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

                source = params[1];
                destination = params[0];


                OutputStream os = conn.getOutputStream();
                os.write(new JSONObject().put("userid",USERINFO.getId()).put("source",params[1]).put("destination",params[0]).toString().getBytes("UTF-8"));
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

            if (s.equals("\"success\"")) {
                dialog.dismiss();
                ModelRoute modelRoute = new ModelRoute();
                modelRoute.setSource(source);
                modelRoute.setDestination(destination);
                modelRoute.setUserid(0);

                modelRoutes.add(modelRoute);
                rvRoute.getAdapter().notifyDataSetChanged();
            } else {
                Alert(ActivityRoute.this, "Some thing went wrong..");
            }

        }
    }

}