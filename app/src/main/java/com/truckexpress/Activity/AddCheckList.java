package com.truckexpress.Activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelChecklist;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddCheckListBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.ChecklistInsert;
import static com.truckexpress.Network.API.Checklistshow;

public class AddCheckList extends AppCompatActivity {
    ActivityAddCheckListBinding activityAddCheckListBinding;
    List<ModelChecklist> modelChecklist = new ArrayList<>();
    private Progress progress;
    private static final String TAG = "AddCheckList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddCheckListBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_check_list);
        progress = new Progress(this);

        activityAddCheckListBinding.rvGoods.setLayoutManager(new LinearLayoutManager(this));
        activityAddCheckListBinding.rvGoods.setHasFixedSize(true);
        activityAddCheckListBinding.rvGoods.addItemDecoration(new MyItemDecoration());
        activityAddCheckListBinding.rvGoods.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(AddCheckList.this).inflate(R.layout.item_checklist,parent,false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                viewHolder.title.setText(modelChecklist.get(position).getCheklist());
            }

            @Override
            public int getItemCount() {
                return modelChecklist.size();
            }
            class ViewHolder extends RecyclerView.ViewHolder {
                TextView title;
                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    title = itemView.findViewById(R.id.tvItem);
                }
            }
        });

        activityAddCheckListBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityAddCheckListBinding.CheckList.getText().toString().isEmpty()){
                    activityAddCheckListBinding.CheckList.setError("Please enter checklist.");
                    Alert(AddCheckList.this,"Please enter checklist.");
                }else {
                    new AddChecklist().execute(activityAddCheckListBinding.CheckList.getText().toString());
                }
            }
        });

        getGoods();
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Add CheckList");
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    private void getGoods() {
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(AddCheckList.this,Checklistshow,entity,"application/json" ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);

                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(result);
                        modelChecklist.clear();

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelChecklist modelState = gson.fromJson(object.toString(), ModelChecklist.class);
                            modelChecklist.add(modelState);
                            activityAddCheckListBinding.rvGoods.getAdapter().notifyDataSetChanged();
                        }
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml","Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml","Progress : "+bytesWritten);
            }
        });
    }

    public class AddChecklist extends AsyncTask<String , Void ,String> {
        HttpURLConnection conn;
        URL url = null;
        String checkList;

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(ChecklistInsert);
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

                checkList = params[0];

                OutputStream os = conn.getOutputStream();
                os.write(new JSONObject().put("userid",USERINFO.getId()).put("checklistname",params[0]).toString().getBytes("UTF-8"));
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
                ModelChecklist modelChecklist1 = new ModelChecklist();
                modelChecklist1.setCheklist(checkList);
                modelChecklist1.setId(modelChecklist.size()+2);
                modelChecklist1.setUserid(0);

                activityAddCheckListBinding.CheckList.setText("");

                modelChecklist.add(modelChecklist1);
                activityAddCheckListBinding.rvGoods.getAdapter().notifyDataSetChanged();
                Alert(AddCheckList.this, "Added Successfully");

            } else {
                Alert(AddCheckList.this, "Some thing went wrong..");
            }

        }
    }


}