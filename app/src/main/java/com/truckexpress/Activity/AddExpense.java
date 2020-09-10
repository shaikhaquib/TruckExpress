package com.truckexpress.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelChecklist;
import com.truckexpress.Models.ModelExpense;
import com.truckexpress.Models.ModelUnit;
import com.truckexpress.Network.API;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddExpenseBinding;

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
import static com.truckexpress.Network.API.ExpanseInsert;
import static com.truckexpress.Network.API.Expenselistuser;
import static com.truckexpress.Network.API.GoodsTypeList;
import static com.truckexpress.Network.API.UnitsList;

public class AddExpense extends AppCompatActivity {
    ActivityAddExpenseBinding activityAddExpenseBinding;
    List<ModelUnit> modelUnits = new ArrayList<>();
    List<ModelExpense> modelExpenses = new ArrayList<>();
    private static final String TAG = "AddExpense";
    Progress progress ;
    String UnitID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddExpenseBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_expense);
        getUnits();
        progress = new Progress(this);
        activityAddExpenseBinding.rvGoods.setLayoutManager(new LinearLayoutManager(this));
        activityAddExpenseBinding.rvGoods.setHasFixedSize(true);
        activityAddExpenseBinding.rvGoods.addItemDecoration(new MyItemDecoration());
        activityAddExpenseBinding.rvGoods.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ViewHolder(LayoutInflater.from(AddExpense.this).inflate(R.layout.item_goods,parent,false));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ViewHolder viewHolder = (ViewHolder)holder;
                viewHolder.title.setText(modelExpenses.get(position).getName());
                viewHolder.subTitle.setText(modelExpenses.get(position).getRate()+" "+modelExpenses.get(position).getUnit());
            }

            @Override
            public int getItemCount() {
                return modelExpenses.size();
            }
            class ViewHolder extends RecyclerView.ViewHolder {
                TextView title;
                TextView subTitle;
                ImageView salesImageView;
                public ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    title = itemView.findViewById(R.id.title);
                    subTitle = itemView.findViewById(R.id.subTitle);
                    salesImageView = itemView.findViewById(R.id.salesImageView);
                    salesImageView.setImageResource(R.drawable.ic_bill);
                }
            }
        });

        activityAddExpenseBinding.unitDrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UnitID = String.valueOf(modelUnits.get(position).getId());
            }
        });

        activityAddExpenseBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityAddExpenseBinding.goods.getText().toString().isEmpty()){
                    activityAddExpenseBinding.goods.setError("Please enter Name.");
                    Alert(AddExpense.this,"Please enter Name.");
                }else
                if (activityAddExpenseBinding.price.getText().toString().isEmpty()){
                    activityAddExpenseBinding.price.setError("Please enter Amount.");
                    Alert(AddExpense.this,"Please enter Amount.");
                }else
                if (activityAddExpenseBinding.unitDrop.getText().toString().isEmpty()){
                    activityAddExpenseBinding.unitDrop.setError("Please Select Unit.");
                    Alert(AddExpense.this,"Please Select Unit.");
                }else
                {
                    new ExpanseInsert().execute(activityAddExpenseBinding.goods.getText().toString(),activityAddExpenseBinding.price.getText().toString(),activityAddExpenseBinding.unitDrop.getText().toString());
                }
            }
        });

        getGoods();
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Add Expense");
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>( AddExpense.this,
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    activityAddExpenseBinding.unitDrop.setAdapter(adapter);

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

        client.post(AddExpense.this,Expenselistuser,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                        modelExpenses.clear();

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelExpense modelState = gson.fromJson(object.toString(), ModelExpense.class);
                            modelExpenses.add(modelState);
                            activityAddExpenseBinding.rvGoods.getAdapter().notifyDataSetChanged();
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

    public class ExpanseInsert extends AsyncTask<String , Void ,String> {
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
                url = new URL(API.ExpanseInsert);
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
                os.write(new JSONObject().put("userid",USERINFO.getId()).put("name",params[0]).put("rate",params[1]).put("unitid",UnitID).toString().getBytes("UTF-8"));
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
                ModelExpense modelExpense = new ModelExpense();
                modelExpense.setName(activityAddExpenseBinding.goods.getText().toString());
                modelExpense.setRate(Integer.parseInt(activityAddExpenseBinding.price.getText().toString().trim()));
                modelExpense.setUnit(activityAddExpenseBinding.unitDrop.getText().toString());

                activityAddExpenseBinding.goods.setText("");
                activityAddExpenseBinding.price.setText("");
                activityAddExpenseBinding.unitDrop.setText("");

                modelExpenses.add(modelExpense);
                activityAddExpenseBinding.rvGoods.getAdapter().notifyDataSetChanged();
                Alert(AddExpense.this, "Added Successfully");

            } else {
                Alert(AddExpense.this, "Some thing went wrong..");
            }

        }
    }


}