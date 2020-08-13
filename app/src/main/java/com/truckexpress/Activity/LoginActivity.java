package com.truckexpress.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.Progress;
import com.truckexpress.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Extras.Constants.readStream;
import static com.truckexpress.Network.API.LOGIN;

public class LoginActivity extends AppCompatActivity {

    Progress progress;
    EditText mobile_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progress = new Progress(this);
        mobile_number = findViewById(R.id.mobile_number);
    }

    public void Authenticate(View view) {
       // startActivity(new Intent(getApplicationContext(),OtpVerification.class));

        if (mobile_number.getText().toString().isEmpty() || mobile_number.getText().toString().length() < 10){
            Alert(LoginActivity.this,"Please Enter Valid Mobile No");
        }else {
            new LoginTask().execute(mobile_number.getText().toString());
        }
    }


    public class LoginTask extends AsyncTask<String , Void ,String> {
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
                url = new URL(LOGIN);
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
                String jsonInputString = "{\n" +
                        "    \"mobile\":\""+params[0]+"\",\n" +
                        "    \"roleid\":3\n" +
                        "}";


                OutputStream os = conn.getOutputStream();
                os.write(jsonInputString.getBytes("UTF-8"));
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
                new MaterialAlertDialogBuilder(LoginActivity.this)
                        .setTitle("Alert")
                        .setMessage("Please Note The OTP \n"+s)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getApplicationContext(),OtpVerification.class).putExtra("mobile",mobile_number.getText().toString()));
                            }
                        })
                        .show();
            }else {
                Alert(LoginActivity.this,"Some thing went wrong..");
            }

        }
    }

}