package com.truckexpress.Activity.Add;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.truckexpress.Models.ModelMotorOwner;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddMoterOwnerBinding;

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
import static com.truckexpress.Network.API.MoterOwnerList;
import static com.truckexpress.Network.API.UploadImageMotorOwner;

public class AddMoterOwner extends AppCompatActivity implements View.OnClickListener{

    ActivityAddMoterOwnerBinding activityAddMoterOwnerBinding;
    private static final String TAG = "AddMoterOwner";
    String document, photo, license;
    Progress progress;
    String isMoterownerDriver = "0";
    List<ModelMotorOwner> motorOwners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       activityAddMoterOwnerBinding= DataBindingUtil.setContentView(this,R.layout.activity_add_moter_owner);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Add Motor Owner");
        actionBar.setDisplayHomeAsUpEnabled(true);
        progress = new Progress(this);

        activityAddMoterOwnerBinding.uploadDocumnet.setOnClickListener(this);
        activityAddMoterOwnerBinding.uploadPhoto.setOnClickListener(this);
        activityAddMoterOwnerBinding.uploadLicense.setOnClickListener(this);
        activityAddMoterOwnerBinding.submit.setOnClickListener(this);
        String[] accountType  = {"Current","Saving"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddMoterOwner.this,
                R.layout.textview,
                accountType);
        activityAddMoterOwnerBinding.Accounttype.setAdapter(adapter);
        activityAddMoterOwnerBinding.Verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityAddMoterOwnerBinding.IFSC.getText().toString().isEmpty()){
                    Alert(AddMoterOwner.this,"Please Enter Ifsc code ");
                }else {
                    getBranchName(activityAddMoterOwnerBinding.IFSC.getText().toString().trim());
                }
            }
        });
        getBanks();

        activityAddMoterOwnerBinding.IFSC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!activityAddMoterOwnerBinding.BranchAdd.getText().toString().isEmpty()){
                    activityAddMoterOwnerBinding.BranchAdd.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityAddMoterOwnerBinding.isDriver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isMoterownerDriver = "1";
                }else {
                    isMoterownerDriver = "0";
                }
            }
        });

        getMotorOwner();
        activityAddMoterOwnerBinding.myMotorOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (motorOwners.size()>0){
                    displayMotorOwner();
                }else {
                    getMotorOwner();
                }
            }
        });
    }

    private void displayMotorOwner() {
        final Dialog dialog  = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_lis_truck);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        RecyclerView rvLOT = dialog.findViewById(R.id.list);
        rvLOT.setLayoutManager(new LinearLayoutManager(this));
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

        TruckNo.setVisibility(View.GONE);
        TruckTyre.setText("Contact No");
        truckType.setText("Name");
        materialTextView.setText("My Motor Owner List");

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
                ModelMotorOwner bidLog = motorOwners.get(position);

                bidHolder.type.setText(bidLog.getFirstname()+" "+bidLog.getLastname());
                bidHolder.number.setText(""+bidLog.getPhone2());
                bidHolder.availibility.setVisibility(View.GONE);
                //bidHolder.availibility.setAllCaps(true);
            }

            @Override
            public int getItemCount() {
                return motorOwners.size();
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

        client.post(AddMoterOwner.this,BranchName,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                            activityAddMoterOwnerBinding.IFSC.setText(object.getString("ifsccode"));
                            activityAddMoterOwnerBinding.BranchAdd.setText(object.getString("branch"));
                        }else {
                            Alert(AddMoterOwner.this,"Please Check weather you have entered correct IFSc code or not");
                            activityAddMoterOwnerBinding.IFSC.setError("Please Check weather you have entered correct IFSc code or not");
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
                    ArrayAdapter<String> adapter = new ArrayAdapter<>( AddMoterOwner.this,
                            R.layout.textview,
                            states.toArray(new String[states.size()]));
                    activityAddMoterOwnerBinding.BankName.setAdapter(adapter);

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

    private boolean isValidate() {
        if (activityAddMoterOwnerBinding.fname.getText().toString().isEmpty()){
            activityAddMoterOwnerBinding.fname.setError(activityAddMoterOwnerBinding.fname.getHint().toString()+" Field Requires");
            Alert(AddMoterOwner.this,activityAddMoterOwnerBinding.fname.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddMoterOwnerBinding.lname.getText().toString().isEmpty()){
            activityAddMoterOwnerBinding.lname.setError(activityAddMoterOwnerBinding.lname.getHint().toString()+" Field Requires");
            Alert(AddMoterOwner.this,activityAddMoterOwnerBinding.lname.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddMoterOwnerBinding.phoneNo.getText().toString().isEmpty()){
            activityAddMoterOwnerBinding.phoneNo.setError(activityAddMoterOwnerBinding.phoneNo.getHint().toString()+" Field Requires");
            Alert(AddMoterOwner.this,activityAddMoterOwnerBinding.phoneNo.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddMoterOwnerBinding.phoneNo.getText().toString().length()<10){
            activityAddMoterOwnerBinding.phoneNo.setError("Please Enter Valid Phone no");
            Alert(AddMoterOwner.this,"Please Enter Valid Phone no");
            return false;
        }else if (activityAddMoterOwnerBinding.emailId.getText().toString().isEmpty()){
            activityAddMoterOwnerBinding.emailId.setError(activityAddMoterOwnerBinding.emailId.getHint().toString()+" Field Requires");
            Alert(AddMoterOwner.this,activityAddMoterOwnerBinding.emailId.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddMoterOwnerBinding.Documentname.getText().toString().isEmpty()){
            activityAddMoterOwnerBinding.Documentname.setError(activityAddMoterOwnerBinding.Documentname.getHint().toString()+" Field Requires");
            Alert(AddMoterOwner.this,activityAddMoterOwnerBinding.Documentname.getHint().toString()+" Field Requires");
            return false;
        }else if (document == null){
            Alert(AddMoterOwner.this,"Please Select Document");
            return false;
        }else if (photo == null){
            Alert(AddMoterOwner.this,"Please Select Photo");
            return false;
        }else if (license == null){
            Alert(AddMoterOwner.this,"Please Select License");
            return false;
        }else {
            return true;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                        Toast.makeText(AddMoterOwner.this, "Storage Permission required to access media", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddMoterOwner.this, "Storage Permission required to access media", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddMoterOwner.this, "Storage Permission required to access media", Toast.LENGTH_SHORT).show();
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
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1211 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            document = filePath;
            compressImage(filePath);
            Glide.with(this).load(filePath).into(activityAddMoterOwnerBinding.imageView6);
        }else if (requestCode == 1212 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            compressImage(filePath);
            Glide.with(this).load(filePath).into(activityAddMoterOwnerBinding.imageView7);
            photo = filePath;
        }else if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            license = filePath;
            compressImage(filePath);
            Glide.with(this).load(filePath).into(activityAddMoterOwnerBinding.imageView8);
        }
    }
    public void postFile() throws FileNotFoundException {

        RequestParams params = new RequestParams();
        params.put("firstname",     activityAddMoterOwnerBinding.fname.getText().toString());
        params.put("lastname",      activityAddMoterOwnerBinding.lname.getText().toString());
        params.put("phone1",        activityAddMoterOwnerBinding.phoneNo.getText().toString());
        params.put("address",        activityAddMoterOwnerBinding.phoneNoop.getText().toString());
        params.put("emailid",       activityAddMoterOwnerBinding.emailId.getText().toString());
        params.put("docname",       activityAddMoterOwnerBinding.Documentname.getText().toString());
        params.put("bankname",      activityAddMoterOwnerBinding.BankName.getText().toString());
        params.put("accountno",     activityAddMoterOwnerBinding.AccountNO.getText().toString());
        params.put("ifsccode",      activityAddMoterOwnerBinding.IFSC.getText().toString());
        params.put("branchaddress", activityAddMoterOwnerBinding.BranchAdd.getText().toString());
        params.put("accounttype",   activityAddMoterOwnerBinding.Accounttype.getText().toString());
        params.put("motorowneranddriver",isMoterownerDriver);
        params.put("userid",        USERINFO.getId());
        params.put("image1", new File(document));
        params.put("image2", new File(photo));
        params.put("image3", new File(license));

        AsyncHttpClient client = new AsyncHttpClient();
        progress.show();

        client.post(UploadImageMotorOwner, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                progress.dismiss();
                Log.d(TAG, "onSuccess: "+result);
                if (result.equals("\"Success\"")) {
                    //    Alert(AddDriver.this, "We have added your request");

                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(AddMoterOwner.this);
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
                    Alert(AddMoterOwner.this, "Some thing went wrong..");
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {

                long progress = (bytesWritten / totalSize)*100;
                Log.d(TAG, "onProgress: "+progress);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] responseBody, Throwable error) {
                // TODO Auto-generated method stub
                String result = new String(responseBody);
                progress.dismiss();
                Log.d(TAG, "OnError: "+result);
                Alert(AddMoterOwner.this, "Some thing went wrong..");
            }
        });
    }
    private void getMotorOwner() {
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

            client.post(this,MoterOwnerList,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                                ModelMotorOwner tyre = gson.fromJson(object.toString(), ModelMotorOwner.class);
                                motorOwners.add(tyre);
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