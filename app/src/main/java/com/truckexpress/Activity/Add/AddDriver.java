package com.truckexpress.Activity.Add;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelCity;
import com.truckexpress.Models.ModelDriver;
import com.truckexpress.Models.ModelState;
import com.truckexpress.Models.Modellunguage;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddDriverBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.compressImage;
import static com.truckexpress.Network.API.BankList;
import static com.truckexpress.Network.API.BranchName;
import static com.truckexpress.Network.API.CityLists;
import static com.truckexpress.Network.API.DriverList;
import static com.truckexpress.Network.API.LunguageList;
import static com.truckexpress.Network.API.StateList;
import static com.truckexpress.Network.API.UploadImageDriver;

public class AddDriver extends AppCompatActivity implements View.OnClickListener {

    ActionBar actionBar;
    ActivityAddDriverBinding activityAddDriverBinding;
    private static final String TAG = "AddDriver";
    List<ModelState> stateList = new ArrayList<>();
    List<ModelCity> cityList = new ArrayList<>();
    List<Modellunguage> modellunguages = new ArrayList<>();
    List<String> Languages =new ArrayList<>();
    String laguageId;
    List<ModelDriver> modelDrivers= new ArrayList<>();

    private static final int REQUEST_WRITE_PERMISSION = 1001;
    List<String> stringBuilder = new ArrayList<>();
    String document, photo, license;
    Progress progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddDriverBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_driver);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Add Driver");
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true);

        progress = new Progress(this);

        getState();
        checkPermission();
        getBanks();
        getDriverList();
        getLang();
        activityAddDriverBinding.Language.setOnClickListener(this);
        activityAddDriverBinding.uploadDocumnet.setOnClickListener(this);
        activityAddDriverBinding.uploadPhoto.setOnClickListener(this);
        activityAddDriverBinding.uploadLicense.setOnClickListener(this);
        activityAddDriverBinding.submit.setOnClickListener(this);
        activityAddDriverBinding.myDrivers.setOnClickListener(this);


        activityAddDriverBinding.stateDrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                try {
                    getCity(stateList.get(position).getStateid());
                    activityAddDriverBinding.CityDrop.clearListSelection();
                    cityList.clear();
                    activityAddDriverBinding.CityDrop.setText("");

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        });

        activityAddDriverBinding.Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityAddDriverBinding.IFSC.getText().toString().isEmpty()){
                    Alert(AddDriver.this,"Please Enter Ifsc code ");
                }else {
                    getBranchName(activityAddDriverBinding.IFSC.getText().toString().trim());
                }
            }
        });
        activityAddDriverBinding.IFSC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!activityAddDriverBinding.BranchAdd.getText().toString().isEmpty()){
                    activityAddDriverBinding.BranchAdd.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        String[] accountType  = {"Current","Saving"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddDriver.this,
                R.layout.textview,
                accountType);
        activityAddDriverBinding.Accounttype.setAdapter(adapter);

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[]
            permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // check whether storage permission granted or not.
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                       // openFilePicker();
                    }
                }
                break;
            default:
                break;
        }
    }

    public Boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(AddDriver.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
            Toast.makeText(this, "Please grant permission to access media", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddDriver.this,
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                    activityAddDriverBinding.stateDrop.setAdapter(adapter);

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
    private void getLang() {
        {
            final Progress progress = new Progress(this);
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();

            client.setTimeout(20 * 1000);
            client.post(LunguageList, new AsyncHttpResponseHandler() {
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
                                Modellunguage modelState = gson.fromJson(object.toString(), Modellunguage.class);
                                modellunguages.add(modelState);
                        }
                    }
                    for (int j = 0; j < modellunguages.size(); j++) {
                        Modellunguage modelState = modellunguages.get(j);
                        Languages.add(modelState.getLunguagename());
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
    }
    private void getCity(int stateid) throws JSONException, UnsupportedEncodingException {
            final Progress progress = new Progress(this);
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(20 * 1000);

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("state_id", stateid);
            StringEntity entity = new StringEntity(jsonParams.toString());

            client.post(AddDriver.this,CityLists,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddDriver.this,
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        activityAddDriverBinding.CityDrop.setAdapter(adapter);


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
    private void getBanks() {
        final Progress progress = new Progress(this);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(20 * 1000);
        client.post(BankList, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);

                Object json = null;
                List<String> states =new ArrayList<>();

                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(result);

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            states.add(object.getString("bankname"));
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>( AddDriver.this,
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    activityAddDriverBinding.BankName.setAdapter(adapter);

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
    private void getBranchName(String s) {
        final Progress progress = new Progress(this);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        JSONObject jsonParams = new JSONObject();
        StringEntity entity = null;
        try {
            jsonParams.put("ifsccode", s);
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(AddDriver.this,BranchName,entity,"application/json" ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);

                Object json = null;
                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {
                        if (new JSONArray(result).length()>0) {
                            JSONObject object = new JSONArray(result).getJSONObject(0);
                            activityAddDriverBinding.IFSC.setText(object.getString("ifsccode"));
                            activityAddDriverBinding.BranchAdd.setText(object.getString("branch"));
                        }else {
                            Alert(AddDriver.this,"Please Check weather you have entered correct IFSc code or not");
                            activityAddDriverBinding.IFSC.setError("Please Check weather you have entered correct IFSc code or not");
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Language:
                List<String> languageID = new ArrayList<>();
                new MaterialAlertDialogBuilder(AddDriver.this)
                        .setTitle("Select Language")
                        .setMultiChoiceItems(Languages.toArray(new String[Languages.size()]), null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b)
                            {
                                //   Toast.makeText(getActivity(), "item clicked at " + i, Toast.LENGTH_SHORT).show();
                                stringBuilder.add(Languages.get(i));
                                languageID.add(String.valueOf(modellunguages.get(i).getId()));
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activityAddDriverBinding.Language.setText(TextUtils.join(",", stringBuilder.toArray()));
                                laguageId = TextUtils.join(",", languageID.toArray());
                                stringBuilder.clear();
                                languageID.clear();
                            }
                        }).show();
                break;
            case R.id.uploadDocumnet:

                Dexter.withContext(this)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.RECORD_AUDIO
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Intent intent1 = new Intent(getApplicationContext(), ImageSelectActivity.class);
                        intent1.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                        intent1.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                        intent1.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                        startActivityForResult(intent1, 1211);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(getApplicationContext(), "Storage Permission required to access media", Toast.LENGTH_SHORT).show();
                    }
                }).check();

                break;
            case R.id.uploadLicense:
                Dexter.withContext(this)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.RECORD_AUDIO
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Intent intent2 = new Intent(getApplicationContext(), ImageSelectActivity.class);
                        intent2.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                        intent2.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                        intent2.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                        startActivityForResult(intent2, 1213);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(getApplicationContext(), "Storage Permission required to access media", Toast.LENGTH_SHORT).show();
                    }
                }).check();


                break;
            case R.id.uploadPhoto:

                Dexter.withContext(this)
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.RECORD_AUDIO
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Intent intent3 = new Intent(getApplicationContext(), ImageSelectActivity.class);
                        intent3.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                        intent3.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                        intent3.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                        startActivityForResult(intent3, 1212);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(getApplicationContext(), "Storage Permission required to access media", Toast.LENGTH_SHORT).show();
                    }
                }).check();

            case R.id.submit:
                if (isValidate()) {
                    try {
                        postFile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.myDrivers:
                if (modelDrivers.size() > 0){
                    displayTrucks();
                }else {
                    getDriverList();
                }
                break;
        }

    }

    private boolean isValidate() {
        if (activityAddDriverBinding.fname.getText().toString().isEmpty()){
            activityAddDriverBinding.fname.setError(activityAddDriverBinding.fname.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.fname.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.lname.getText().toString().isEmpty()){
            activityAddDriverBinding.lname.setError(activityAddDriverBinding.lname.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.lname.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.phoneNo.getText().toString().isEmpty()){
            activityAddDriverBinding.phoneNo.setError(activityAddDriverBinding.phoneNo.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.phoneNo.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.phoneNo.getText().toString().length()<10){
            activityAddDriverBinding.phoneNo.setError("Please Enter Valid Phone no");
            Alert(AddDriver.this,"Please Enter Valid Phone no");
             return false;
        }else if (activityAddDriverBinding.stateDrop.getText().toString().isEmpty()){
            activityAddDriverBinding.stateDrop.setError(activityAddDriverBinding.stateDrop.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.stateDrop.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.CityDrop.getText().toString().isEmpty()){
            activityAddDriverBinding.CityDrop.setError(activityAddDriverBinding.CityDrop.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.CityDrop.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.address.getText().toString().isEmpty()){
            activityAddDriverBinding.address.setError(activityAddDriverBinding.address.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.address.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.pincode.getText().toString().isEmpty()){
            activityAddDriverBinding.pincode.setError(activityAddDriverBinding.pincode.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.pincode.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.Language.getText().toString().isEmpty()){
            activityAddDriverBinding.Language.setError(activityAddDriverBinding.Language.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.Language.getHint().toString()+" Field Requires");
             return false;
        }else if (activityAddDriverBinding.Documentname.getText().toString().isEmpty()){
            activityAddDriverBinding.Documentname.setError(activityAddDriverBinding.Documentname.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.Documentname.getHint().toString()+" Field Requires");
             return false;
        }else if (document == null){
            Alert(AddDriver.this,"Please Select Document");
            return false;
        }else if (photo == null){
            Alert(AddDriver.this,"Please Select Photo");
            return false;
        }else if (license == null){
            Alert(AddDriver.this,"Please Select License");
            return false;
        }/*else if (activityAddDriverBinding.BankName.getText().toString().isEmpty()){
            activityAddDriverBinding.BankName.setError(activityAddDriverBinding.BankName.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.BankName.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddDriverBinding.AccountNO.getText().toString().isEmpty()){
            activityAddDriverBinding.AccountNO.setError(activityAddDriverBinding.AccountNO.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.AccountNO.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddDriverBinding.IFSC.getText().toString().isEmpty()){
            activityAddDriverBinding.IFSC.setError(activityAddDriverBinding.IFSC.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.IFSC.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddDriverBinding.BranchAdd.getText().toString().isEmpty()){
            activityAddDriverBinding.BranchAdd.setError(activityAddDriverBinding.BranchAdd.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.BranchAdd.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddDriverBinding.Accounttype.getText().toString().isEmpty()){
            activityAddDriverBinding.Accounttype.setError(activityAddDriverBinding.Accounttype.getHint().toString()+" Field Requires");
            Alert(AddDriver.this,activityAddDriverBinding.Accounttype.getHint().toString()+" Field Requires");
            return false;
        }*/else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1211 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            document = filePath;
            compressImage(filePath);
            Glide.with(this).load(filePath).into(activityAddDriverBinding.imageView6);
        }else if (requestCode == 1212 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            compressImage(filePath);
            Glide.with(this).load(filePath).into(activityAddDriverBinding.imageView7);
            photo = filePath;
        }else if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            license = filePath;
            compressImage(filePath);
            Glide.with(this).load(filePath).into(activityAddDriverBinding.imageView8);
        }
    }


    public void postFile() throws FileNotFoundException {

        RequestParams params = new RequestParams();
        params.put("firstname",     activityAddDriverBinding.fname.getText().toString());
        params.put("lastname",      activityAddDriverBinding.lname.getText().toString());
        params.put("phone1",        activityAddDriverBinding.phoneNo.getText().toString());
        params.put("phone2",        activityAddDriverBinding.phoneNoop.getText().toString());

        for (int i = 0; i < stateList.size(); i++) {
            ModelState modelState = stateList.get(i);
            if (modelState.getState().equals(activityAddDriverBinding.stateDrop.getText().toString().trim())){
                params.put("state",   modelState.getStateid()      );
            }
        }
        for (int i = 0; i < cityList.size(); i++) {
            ModelCity modelCity = cityList.get(i);
            if (modelCity.getCityName().equals(activityAddDriverBinding.CityDrop.getText().toString().trim())){
                params.put("city",          cityList.get(i).getCityId());
            }
        }
        params.put("address",       activityAddDriverBinding.address.getText().toString());
        params.put("pincode",       activityAddDriverBinding.pincode.getText().toString());
        params.put("docname",       activityAddDriverBinding.Documentname.getText().toString());
        params.put("bankname",      activityAddDriverBinding.BankName.getText().toString());
        params.put("accountno",     activityAddDriverBinding.AccountNO.getText().toString());
        params.put("ifsccode",      activityAddDriverBinding.IFSC.getText().toString());
        params.put("branchaddress", activityAddDriverBinding.BranchAdd.getText().toString());
        params.put("accounttype",   activityAddDriverBinding.Accounttype.getText().toString());
        params.put("userid",        USERINFO.getId());
        params.put("pincode",       activityAddDriverBinding.pincode.getText().toString());
        params.put("Image1", new File(document));
        params.put("Image2", new File(photo));
        params.put("Image3", new File(license));

        AsyncHttpClient client = new AsyncHttpClient();
        progress.show();

        client.post(UploadImageDriver, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                progress.dismiss();
                Log.d(TAG, "onSuccess: "+result);
                if (result.equals("\"Success\"")) {
                //    Alert(AddDriver.this, "We have added your request");

                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(AddDriver.this);
                    dialogBuilder.setTitle("Success");
                    dialogBuilder.setMessage("We have added your request");
                    dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialogBuilder.show();


                } else {
                    Alert(AddDriver.this, "Some thing went wrong..");
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

                long progress = (bytesWritten / totalSize)*100;
          //      Log.d(TAG, "onProgress: "+progress);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                // TODO Auto-generated method stub
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onFailure: "+result);
                Alert(AddDriver.this, "Some thing went wrong..");
            }
        });
    }

    private void displayTrucks() {
        final Dialog dialog  = new Dialog(AddDriver.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_lis_truck);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        RecyclerView rvLOT = dialog.findViewById(R.id.list);
        rvLOT.setLayoutManager(new LinearLayoutManager(AddDriver.this));
        rvLOT.hasFixedSize();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        TextView TruckNo,TruckTyre,truckType,materialTextView;
        TruckNo = dialog.findViewById(R.id.TruckNo);
        TruckTyre = dialog.findViewById(R.id.TruckTyre);
        truckType = dialog.findViewById(R.id.truckType);
        materialTextView = dialog.findViewById(R.id.materialTextView);

        TruckNo.setText("Action");
        TruckTyre.setText("Contact No");
        truckType.setText("Name");
        materialTextView.setText("My Driver List");

        dialog.findViewById(R.id.submit).setVisibility(View.GONE);
        dialog.findViewById(R.id.select).setVisibility(View.GONE);

        dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        rvLOT.addItemDecoration(new MyItemDecoration());

        rvLOT.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.truck_list, parent, false);
                return new ListHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ListHolder bidHolder = (ListHolder)holder;
                ModelDriver bidLog = modelDrivers.get(position);

                bidHolder.type.setText(bidLog.getFirstname()+" "+bidLog.getLastname());
                bidHolder.number.setText(bidLog.getPhone1());
                bidHolder.availibility.setText("WIP");
                //bidHolder.availibility.setAllCaps(true);
            }

            @Override
            public int getItemCount() {
                return modelDrivers.size();
            }

            class ListHolder extends RecyclerView.ViewHolder {
                TextView type, number, availibility;
                CheckBox checkBox;
                public ListHolder(@NonNull View itemView) {
                    super(itemView);
                    type = itemView.findViewById(R.id.type);
                    number = itemView.findViewById(R.id.Number);
                    availibility = itemView.findViewById(R.id.Availibility);
                    checkBox = itemView.findViewById(R.id.checkbox);
                    checkBox.setVisibility(View.GONE);
                }
            }
        });
    }
    private void getDriverList() {
        {
            final Progress progress = new Progress(this);
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(20 * 1000);

            StringEntity entity = null;
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("userid",USERINFO.getId());
                entity = new StringEntity(jsonParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(AddDriver.this,DriverList,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                                ModelDriver tyre = gson.fromJson(object.toString(), ModelDriver.class);
                                modelDrivers.add(tyre);
                            }
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i("xml","Sending failed");
                    String result = new String(bytes);
                    Log.d(TAG, "onSuccess: "+result);
                    progress.dismiss();
                }

            });
        }
    }


}