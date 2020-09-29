package com.truckexpress.Activity.Add;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.truckexpress.Models.ModelGoodType;
import com.truckexpress.Models.ModelGoods;
import com.truckexpress.Models.ModelUnit;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddGoodTypeBinding;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.GoodsTypeList;
import static com.truckexpress.Network.API.GoodsTypefullList;
import static com.truckexpress.Network.API.Transportersgoodtype;
import static com.truckexpress.Network.API.UnitsList;

public class AddGoodType extends AppCompatActivity {
    ActivityAddGoodTypeBinding activityAddGoodTypeBinding;
    List<ModelUnit> modelUnits = new ArrayList<>();
    List<ModelGoods> modelGoods = new ArrayList<>();
    List<ModelGoodType> modelGoodTypes = new ArrayList<>();
    private static final String TAG = "AddGoodType";
    Progress progress;
    String UnitID,GoodTypeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddGoodTypeBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_good_type);
        getUnits();
        progress = new Progress(this);
        activityAddGoodTypeBinding.rvGoods.setLayoutManager(new LinearLayoutManager(this));
        activityAddGoodTypeBinding.rvGoods.setHasFixedSize(true);
        activityAddGoodTypeBinding.rvGoods.addItemDecoration(new MyItemDecoration());
        activityAddGoodTypeBinding.rvGoods.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(AddGoodType.this).inflate(R.layout.item_goods,parent,false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                viewHolder.title.setText(modelGoods.get(position).getGoodsname());
                viewHolder.subTitle.setText(modelGoods.get(position).getPrice()+" "+modelGoods.get(position).getUnits());
            }

            @Override
            public int getItemCount() {
                return modelGoods.size();
            }
            class ViewHolder extends RecyclerView.ViewHolder {
                TextView title;
                TextView subTitle;
                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    title = itemView.findViewById(R.id.title);
                    subTitle = itemView.findViewById(R.id.subTitle);
                }
            }
        });

        getGoods();
        getGoodsType();
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Add Goods Type");
        actionBar.setDisplayHomeAsUpEnabled(true);

        activityAddGoodTypeBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityAddGoodTypeBinding.Goods.getText().toString().isEmpty()){
                    activityAddGoodTypeBinding.Goods.setError("Please enter Name.");
                    Alert(AddGoodType.this,"Please enter Name.");
                }else
                if (activityAddGoodTypeBinding.price.getText().toString().isEmpty()){
                    activityAddGoodTypeBinding.price.setError("Please enter Amount.");
                    Alert(AddGoodType.this,"Please enter Amount.");
                }else
                if (activityAddGoodTypeBinding.unitDrop.getText().toString().isEmpty()){
                    activityAddGoodTypeBinding.unitDrop.setError("Please Select Unit.");
                    Alert(AddGoodType.this,"Please Select Unit.");
                }else
                if (activityAddGoodTypeBinding.HSNCode.getText().toString().isEmpty()){
                    activityAddGoodTypeBinding.HSNCode.setError("Please enter HSN Code.");
                    Alert(AddGoodType.this,"Please enter HSN Code.");
                }else
                {
                    new GoodTypeInsert().execute(activityAddGoodTypeBinding.Goods.getText().toString(),activityAddGoodTypeBinding.price.getText().toString(),activityAddGoodTypeBinding.unitDrop.getText().toString(),activityAddGoodTypeBinding.HSNCode.getText().toString());
                }
            }
        });
        activityAddGoodTypeBinding.unitDrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UnitID = String.valueOf(modelUnits.get(position).getId());
            }
        });
        activityAddGoodTypeBinding.Goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GoodTypeID = String.valueOf(modelGoodTypes.get(position).getId());
            }
        });



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

    private void getUnits() {
            final Progress progress = new Progress(this);
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();

            client.setTimeout(20 * 1000);
            client.post(UnitsList, new AsyncHttpResponseHandler() {
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

                            for (int y = 0; y < jsonArray.length(); y++) {
                                JSONObject object = jsonArray.getJSONObject(y);
                                Gson gson = new Gson();
                                ModelUnit modelState = gson.fromJson(object.toString(), ModelUnit.class);
                                modelUnits.add(modelState);
                            }
                        }
                        List<String> states =new ArrayList<>();
                        for (int j = 0; j < modelUnits.size(); j++) {
                            ModelUnit modelState = modelUnits.get(j);
                            states.add(modelState.getUnit());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddGoodType.this,
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        activityAddGoodTypeBinding.unitDrop.setAdapter(adapter);

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
    private void getGoods() {
        final Progress progress = new Progress(this);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            jsonParams.put("dash", "dash");
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(AddGoodType.this,GoodsTypeList,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                        modelGoods.clear();

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelGoods modelState = gson.fromJson(object.toString(), ModelGoods.class);
                            modelGoods.add(modelState);
                            activityAddGoodTypeBinding.rvGoods.getAdapter().notifyDataSetChanged();
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
    private void getGoodsType() {
        final Progress progress = new Progress(this);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(20 * 1000);
        client.post(GoodsTypefullList, new AsyncHttpResponseHandler() {
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

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelGoodType modelState = gson.fromJson(object.toString(), ModelGoodType.class);
                            modelGoodTypes.add(modelState);
                        }
                    }
                    List<String> states =new ArrayList<>();
                    for (int j = 0; j < modelGoodTypes.size(); j++) {
                        ModelGoodType modelState = modelGoodTypes.get(j);
                        states.add(modelState.getGoodsname());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>( AddGoodType.this,
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    activityAddGoodTypeBinding.Goods.setAdapter(adapter);

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

    public class GoodTypeInsert extends AsyncTask<String , Void ,String> {
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
                url = new URL(Transportersgoodtype);
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
                JSONObject object = new JSONObject().put("userid",USERINFO.getId())
                        .put("goodtypeid",GoodTypeID)
                        .put("goodtypename",params[0])
                        .put("price",params[1])
                        .put("units",UnitID)
                        .put("hsncode",params[3]);
                OutputStream os = conn.getOutputStream();
                Log.d(TAG, "doInBackground: " + object.toString());
                os.write(object.toString().getBytes(StandardCharsets.UTF_8));
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
            Log.d(TAG, "onPostExecute: "+s);

            if (s.equals("\"success\"")) {
                ModelGoods modelExpense = new ModelGoods();
                modelExpense.setGoodsname(activityAddGoodTypeBinding.Goods.getText().toString());
                modelExpense.setPrice(activityAddGoodTypeBinding.price.getText().toString().trim());
                modelExpense.setUnits(activityAddGoodTypeBinding.unitDrop.getText().toString());

                activityAddGoodTypeBinding.Goods.setText("");
                activityAddGoodTypeBinding.price.setText("");
                activityAddGoodTypeBinding.unitDrop.setText("");

                modelGoods.add(modelExpense);
                activityAddGoodTypeBinding.rvGoods.getAdapter().notifyDataSetChanged();
                Alert(AddGoodType.this, "Added Successfully");

            } else {
                Alert(AddGoodType.this, "Some thing went wrong..");
            }

        }
    }





}