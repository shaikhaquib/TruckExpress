package com.truckexpress.Activity.Add;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelCity;
import com.truckexpress.Models.ModelState;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddCompanyBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Network.API.CityLists;
import static com.truckexpress.Network.API.StateList;

public class AddCompany extends AppCompatActivity {

    ActivityAddCompanyBinding activityAddCompanyBinding;
    List<ModelState> stateList = new ArrayList<>();
    List<ModelCity> cityList = new ArrayList<>();
    private static final String TAG = "AddCompany";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddCompanyBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_company);

        getState();
       ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Company");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);


        activityAddCompanyBinding.DOJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialog.Builder(AddCompany.this)
                        .bottomSheet()
                        .curved()
                        .displayMinutes(false)
                        .displayHours(false)
                        .displayDays(false)
                        .displayMonth(true)
                        .displayYears(true)
                        .title("Select Date Of Joining")
                        .displayDaysOfMonth(true)
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                activityAddCompanyBinding.DOJ.setText(new SimpleDateFormat("dd MMM yyyy").format(date));
                            }
                        })
                        .display();

            }
        });

        activityAddCompanyBinding.stateDrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                try {
                    getCity(stateList.get(position).getStateid());
                    activityAddCompanyBinding.CityDrop.clearListSelection();
                    cityList.clear();
                    activityAddCompanyBinding.CityDrop.setText("");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

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
    private void getState() {
        {
            final Progress progress = new Progress(this);
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();

            client.setTimeout(20 * 1000);
            client.post(StateList, new AsyncHttpResponseHandler() {
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
                                ModelState modelState = gson.fromJson(object.toString(), ModelState.class);
                                stateList.add(modelState);
                            }
                        }
                        List<String> states =new ArrayList<>();
                        for (int j = 0; j < stateList.size(); j++) {
                            ModelState modelState = stateList.get(j);
                            states.add(modelState.getState());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddCompany.this,
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        activityAddCompanyBinding.stateDrop.setAdapter(adapter);

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
    }
    private void getCity(int stateid) throws JSONException, UnsupportedEncodingException {
        final Progress progress = new Progress(this);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        JSONObject jsonParams = new JSONObject();
        jsonParams.put("state_id", stateid);
        StringEntity entity = new StringEntity(jsonParams.toString());

        client.post(AddCompany.this,CityLists,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                        cityList.clear();

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelCity modelState = gson.fromJson(object.toString(), ModelCity.class);
                            cityList.add(modelState);
                        }
                    }
                    List<String> states =new ArrayList<>();
                    for (int j = 0; j < cityList.size(); j++) {
                        ModelCity modelState = cityList.get(j);
                        states.add(modelState.getCityName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>( AddCompany.this,
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    activityAddCompanyBinding.CityDrop.setAdapter(adapter);


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

}