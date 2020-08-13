package com.truckexpress.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.truckexpress.Extras.AppExecutor;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.CustomPinview;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.UserInfo;
import com.truckexpress.R;
import com.truckexpress.Room.SessionManager;
import com.truckexpress.Room.UserDatabase;

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

import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Extras.Constants.hideKeyboardFrom;
import static com.truckexpress.Network.API.VERIFY_OTP;

public class OtpVerification extends AppCompatActivity {

    Progress progress;
    CustomPinview customPinview;
    private static final String TAG = "OtpVerification";
    UserDatabase userDatabase;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        progress = new Progress(this);
        customPinview = findViewById(R.id.enterPin);
        userDatabase = Room.databaseBuilder(getApplicationContext(),UserDatabase.class, Constants.DATABASE_NAME).fallbackToDestructiveMigration().build();
        sessionManager = new SessionManager(this);
        customPinview.setPinViewEventListener(new CustomPinview.PinViewEventListener() {
            @Override
            public void onDataEntered(CustomPinview pinview, boolean fromUser) {
                hideKeyboardFrom(getApplicationContext(),customPinview);
            }
        });
    }

    public void Authenticate(View view) {
        if (customPinview.getValue().length() < 4){
            Alert(OtpVerification.this,"Please Enter Valid OTP");
        }else {
            new OTPAuth().execute("3",getIntent().getStringExtra("mobile"),customPinview.getValue());
        }
    }

    public class OTPAuth extends AsyncTask<String , Void ,String> {

        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(VERIFY_OTP);
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

                JSONObject object = new JSONObject();
                object.put("mobile",params[1]);
                object.put("roleid",params[0]);
                object.put("OTP",params[2]);



                OutputStream os = conn.getOutputStream();
                os.write(object.toString().getBytes("UTF-8"));
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

            if (!s.equals("unsuccessful")){
                try {
                    Object json = new JSONTokener(s).nextValue();
                    if(json instanceof JSONArray){
                     JSONArray jsonArray = (JSONArray)json;
                     JSONObject userData = jsonArray.getJSONObject(0);

                        Gson gson = new Gson();
                        final UserInfo data = gson.fromJson(userData.toString(), UserInfo.class);
                        if (data.getErrorStatus().equals("false")) {
                            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    userDatabase.dbAccess().insertUser(data);
                                }
                            });
                            sessionManager.setLogin(true);
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else {
                            Alert(OtpVerification.this,data.getUIMessage());
                        }
                    }else {
                        Alert(OtpVerification.this,s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onPostExecute: ",e );
                }


            }else {
                Alert(OtpVerification.this,"Some thing went wrong..");
            }

        }
    }

}