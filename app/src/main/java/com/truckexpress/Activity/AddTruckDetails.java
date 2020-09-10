package com.truckexpress.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ArrayItem;
import com.truckexpress.Models.ModelCity;
import com.truckexpress.Models.ModelCurrentBooking;
import com.truckexpress.Models.ModelDriver;
import com.truckexpress.Models.ModelState;
import com.truckexpress.Models.ModelTruck;
import com.truckexpress.Models.ModelTruckList;
import com.truckexpress.Models.ModelTruckType;
import com.truckexpress.Models.ModelTyre;
import com.truckexpress.Models.ModelWeight;
import com.truckexpress.R;
import com.truckexpress.databinding.ActivityAddTruckDetailsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Network.API.CityLists;
import static com.truckexpress.Network.API.DriverList;
import static com.truckexpress.Network.API.StateList;
import static com.truckexpress.Network.API.TruckListByUserID;
import static com.truckexpress.Network.API.TruckTypeList;
import static com.truckexpress.Network.API.TyresList;
import static com.truckexpress.Network.API.UploadImage;
import static com.truckexpress.Network.API.UploadImageDriver;
import static com.truckexpress.Network.API.WeightList;

public class AddTruckDetails extends AppCompatActivity implements View.OnClickListener{
    ActivityAddTruckDetailsBinding activityAddTruckDetailsBinding;
    private static final String TAG = "AddTruckDetails";
    Progress progress;
    List<ModelTruckType> truckTypeList= new ArrayList<>();
    List<ModelTyre> modelTyres= new ArrayList<>();
    List<ModelWeight> weightList= new ArrayList<>();
    List<ModelDriver> modelDrivers= new ArrayList<>();
    List<ModelTruckList> truckLists= new ArrayList<>();
    String rcBook, PAN, PUC;
    List<String> stringBuilder = new ArrayList<>();
    List<String> driversID = new ArrayList<>();
    String stringIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddTruckDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_truck_details);
        progress = new Progress(this);

        getTuckType();
        activityAddTruckDetailsBinding.RCBook.setOnClickListener(this);
        activityAddTruckDetailsBinding.uploadPan.setOnClickListener(this);
        activityAddTruckDetailsBinding.uploadPUC.setOnClickListener(this);
        activityAddTruckDetailsBinding.Permit.setOnClickListener(this);
        activityAddTruckDetailsBinding.driver.setOnClickListener(this);
        activityAddTruckDetailsBinding.submit.setOnClickListener(this);

        activityAddTruckDetailsBinding.TruckType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getTyres(truckTypeList.get(position).getId());
                activityAddTruckDetailsBinding.TruckTyre.clearListSelection();
                truckTypeList.clear();
                activityAddTruckDetailsBinding.TruckTyre.setText("");
                activityAddTruckDetailsBinding.TruckWeight.clearListSelection();
                weightList.clear();
                activityAddTruckDetailsBinding.TruckTyre.setText("");
            }
        });
        activityAddTruckDetailsBinding.TruckTyre.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getWeight(modelTyres.get(position).getId());
                activityAddTruckDetailsBinding.TruckWeight.clearListSelection();
                weightList.clear();
                activityAddTruckDetailsBinding.TruckWeight.setText("");
            }
        });

        getDriverList();
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setTitle("Add Truck Details");
        actionBar.setDisplayHomeAsUpEnabled(true);

        getTruckList();

        activityAddTruckDetailsBinding.myTrucks.setOnClickListener(this);
    }

    private void getTruckList() {
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

            client.post(AddTruckDetails.this,TruckListByUserID,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                                ModelTruckList tyre = gson.fromJson(object.toString(), ModelTruckList.class);
                                truckLists.add(tyre);
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

    private void getTuckType() {
        {
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();

            client.setTimeout(20 * 1000);
            client.post(TruckTypeList, new AsyncHttpResponseHandler() {
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
                                ModelTruckType modelState = gson.fromJson(object.toString(), ModelTruckType.class);
                                truckTypeList.add(modelState);
                            }
                        }
                        List<String> type =new ArrayList<>();
                        for (int j = 0; j < truckTypeList.size(); j++) {
                            ModelTruckType modelState = truckTypeList.get(j);
                            type.add(modelState.getTrucktype());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddTruckDetails.this,
                                R.layout.textview,
                                type.toArray(new String[type.size()]));
                        activityAddTruckDetailsBinding.TruckType.setAdapter(adapter);

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
    private void getTyres(int id) {
        {
            final Progress progress = new Progress(this);
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();

            StringEntity entity = null;
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("truckid", id);
                entity = new StringEntity(jsonParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(AddTruckDetails.this,TyresList,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                            modelTyres.clear();

                            for (int y = 0; y < jsonArray.length(); y++) {
                                JSONObject object = jsonArray.getJSONObject(y);
                                Gson gson = new Gson();
                                ModelTyre tyre = gson.fromJson(object.toString(), ModelTyre.class);
                                modelTyres.add(tyre);
                            }
                        }
                        List<String> states =new ArrayList<>();
                        for (int j = 0; j < modelTyres.size(); j++) {
                            ModelTyre tyre = modelTyres.get(j);
                            states.add(tyre.getNooftyres());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddTruckDetails.this,
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        activityAddTruckDetailsBinding.TruckTyre.setAdapter(adapter);


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
    private void getWeight(int id) {
        {
            final Progress progress = new Progress(this);
            progress.show();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(20 * 1000);

            StringEntity entity = null;
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("tyresid", id);
                entity = new StringEntity(jsonParams.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            client.post(AddTruckDetails.this,WeightList,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                            weightList.clear();

                            for (int y = 0; y < jsonArray.length(); y++) {
                                JSONObject object = jsonArray.getJSONObject(y);
                                Gson gson = new Gson();
                                ModelWeight tyre = gson.fromJson(object.toString(), ModelWeight.class);
                                weightList.add(tyre);
                            }
                        }
                        List<String> states =new ArrayList<>();
                        for (int j = 0; j < weightList.size(); j++) {
                            ModelWeight tyre = weightList.get(j);
                            states.add(tyre.getWeight()+" Ton");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>( AddTruckDetails.this,
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        activityAddTruckDetailsBinding.TruckWeight.setAdapter(adapter);


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

            client.post(AddTruckDetails.this,DriverList,entity,"application/json" ,new AsyncHttpResponseHandler() {
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
                    progress.dismiss();
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    Log.i("xml","Progress : "+bytesWritten);
                }
            });
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.RCBook:
                    Intent intent1 = new Intent(getApplicationContext(), ImageSelectActivity.class);
                    intent1.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                    intent1.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                    intent1.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                    startActivityForResult(intent1, 1211);
                break;
            case R.id.uploadPan:
                Intent intent2 = new Intent(getApplicationContext(), ImageSelectActivity.class);
                    intent2.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                    intent2.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                    intent2.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                    startActivityForResult(intent2, 1213);
                break;
            case R.id.uploadPUC:
                    Intent intent3 = new Intent(getApplicationContext(), ImageSelectActivity.class);
                    intent3.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
                    intent3.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
                    intent3.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
                    startActivityForResult(intent3, 1212);
                break;
            case R.id.Permit:
                String[] s = {"National","Other"};
                new MaterialAlertDialogBuilder(AddTruckDetails.this)
                        .setTitle("Select Permit")
                        .setItems(s, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activityAddTruckDetailsBinding.Permit.setText(s[which]);
                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
                case R.id.driver:
                    ArrayList<MultiSelectModel> driversName = new ArrayList<>();
                    for (int i = 0; i < modelDrivers.size(); i++) {
                        ModelDriver modelDriver = modelDrivers.get(i);
                        driversName.add(new MultiSelectModel(modelDriver.getId(),modelDriver.getFirstname()+" "+modelDriver.getLastname()));
                    }

                  new MultiSelectDialog()
                          .setMaxSelectionLimit(2)
                          .title("Select Driver")
                          .positiveText("Done")
                          .multiSelectList(driversName)
                          .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                              @Override
                              public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String s) {
                                  for (int i = 0; i < selectedIds.size(); i++) {
                                      stringBuilder.add(selectedNames.get(i));
                                      driversID.add(String.valueOf(selectedIds.get(i)));
                                  }
                                  activityAddTruckDetailsBinding.driver.setText(TextUtils.join(",",stringBuilder.toArray()));
                                  stringIds = TextUtils.join(",",driversID.toArray());
                                  stringBuilder.clear();
                                  driversID.clear();
                              }

                              @Override
                              public void onCancel() {

                              }
                          }).show(getSupportFragmentManager(),TAG);
                    break;
            case R.id.submit:
                if (isValidate()){
                    try {
                        postFile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.myTrucks:
                if (truckLists.size() > 0){
                    displayTrucks();
                }else {
                    getTruckList();
                }
                break;
        }
    }

    private void displayTrucks() {
        final Dialog dialog  = new Dialog(AddTruckDetails.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_lis_truck);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        RecyclerView rvLOT = dialog.findViewById(R.id.list);
        rvLOT.setLayoutManager(new LinearLayoutManager(AddTruckDetails.this));
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
        rvLOT.addItemDecoration(new MyItemDecoration());

        TruckNo.setText("Truck No");
        TruckTyre.setText("Truck Tyre");
        truckType.setText("Truck Type");
        materialTextView.setText("My Truck List");

        dialog.findViewById(R.id.submit).setVisibility(View.GONE);
        dialog.findViewById(R.id.select).setVisibility(View.GONE);

        dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

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
                ModelTruckList bidLog = truckLists.get(position);

                bidHolder.type.setText(bidLog.getTrucktype());
                bidHolder.number.setText(bidLog.getTyres());
                bidHolder.availibility.setText(""+bidLog.getTruckname());
                bidHolder.availibility.setAllCaps(true);

            }

            @Override
            public int getItemCount() {
                return truckLists.size();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1211 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            activityAddTruckDetailsBinding.imageView6.setImageBitmap(selectedImage);
            rcBook = filePath;
        }else if (requestCode == 1212 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            activityAddTruckDetailsBinding.imageView8.setImageBitmap(selectedImage);
            PUC = filePath;
        }else if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            activityAddTruckDetailsBinding.imageView7.setImageBitmap(selectedImage);
            PAN = filePath;
        }
    }

    public void postFile() throws FileNotFoundException {

        RequestParams params = new RequestParams();



        for (int i = 0; i < truckTypeList.size(); i++) {
            ModelTruckType modelTruckType = truckTypeList.get(i);
            if (modelTruckType.getTrucktype().equals(activityAddTruckDetailsBinding.TruckTyre.getText().toString().trim())){
                params.put("trucktype",   modelTruckType.getId()      );
            }
        }
        for (int i = 0; i < modelTyres.size(); i++) {
            ModelTyre tyre = modelTyres.get(i);
            if (tyre.getNooftyres().equals(activityAddTruckDetailsBinding.TruckTyre.getText().toString().trim())){
                params.put("tyertype",    tyre.getId());
            }
        }
        for (int i = 0; i < weightList.size(); i++) {
            ModelWeight modelWeight = weightList.get(i);
            if (String.valueOf(modelWeight.getWeight()).equals(activityAddTruckDetailsBinding.TruckWeight.getText().toString().trim())){
                params.put("weights",      modelWeight.getId());
            }
        }

        params.put("permit",        activityAddTruckDetailsBinding.Permit.getText().toString());
        params.put("trucknumber",        activityAddTruckDetailsBinding.TruckNo.getText().toString());
        params.put("userid",        USERINFO.getId());
        params.put("driverid",      stringIds);
        params.put("Image1", new File(rcBook));
        params.put("Image2", new File(PAN));
        params.put("Image3", new File(PUC));

        AsyncHttpClient client = new AsyncHttpClient();
        progress.show();

        client.post(UploadImage, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                progress.dismiss();
                Log.d(TAG, "onSuccess: "+result);
                if (result.equals("\"Success\"")) {
                    //    Alert(AddTruckDetails.this, "We have added your request");

                    MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(AddTruckDetails.this);
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
                    Alert(AddTruckDetails.this, "Some thing went wrong..");
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
                progress.dismiss();
                Alert(AddTruckDetails.this, "Some thing went wrong..");
            }
        });
    }

    private boolean isValidate() {
        Pattern pattern1=Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$");
        Matcher matcher1=pattern1.matcher(activityAddTruckDetailsBinding.TruckNo.getText().toString());

        if (activityAddTruckDetailsBinding.TruckType.getText().toString().isEmpty()){
            activityAddTruckDetailsBinding.TruckType.setError(activityAddTruckDetailsBinding.TruckType.getHint().toString()+" Field Requires");
            Alert(AddTruckDetails.this,activityAddTruckDetailsBinding.TruckType.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddTruckDetailsBinding.TruckTyre.getText().toString().isEmpty()){
            activityAddTruckDetailsBinding.TruckTyre.setError(activityAddTruckDetailsBinding.TruckTyre.getHint().toString()+" Field Requires");
            Alert(AddTruckDetails.this,activityAddTruckDetailsBinding.TruckTyre.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddTruckDetailsBinding.TruckWeight.getText().toString().isEmpty()){
            activityAddTruckDetailsBinding.TruckWeight.setError(activityAddTruckDetailsBinding.TruckWeight.getHint().toString()+" Field Requires");
            Alert(AddTruckDetails.this,activityAddTruckDetailsBinding.TruckWeight.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddTruckDetailsBinding.TruckNo.getText().toString().isEmpty()){
            activityAddTruckDetailsBinding.TruckNo.setError(activityAddTruckDetailsBinding.TruckNo.getHint().toString()+" Field Requires");
            Alert(AddTruckDetails.this,activityAddTruckDetailsBinding.TruckNo.getHint().toString()+" Field Requires");
            return false;
        }else if (!matcher1.matches() && !activityAddTruckDetailsBinding.TruckNo.getText().toString().isEmpty()) {
            activityAddTruckDetailsBinding.TruckNo.setError("Please Enter Valid Truck no");
            Alert(AddTruckDetails.this,"Please Enter Valid Truck no");
        }else if (rcBook == null){
            Alert(AddTruckDetails.this,"Please Select Document");
            return false;
        }else if (PAN == null){
            Alert(AddTruckDetails.this,"Please Select Photo");
            return false;
        }else if (PUC == null){
            Alert(AddTruckDetails.this,"Please Select License");
            return false;
        }else if (activityAddTruckDetailsBinding.Permit.getText().toString().isEmpty()){
            activityAddTruckDetailsBinding.Permit.setError(activityAddTruckDetailsBinding.Permit.getHint().toString()+" Field Requires");
            Alert(AddTruckDetails.this,activityAddTruckDetailsBinding.Permit.getHint().toString()+" Field Requires");
            return false;
        }else if (activityAddTruckDetailsBinding.driver.getText().toString().isEmpty()){
            activityAddTruckDetailsBinding.driver.setError(activityAddTruckDetailsBinding.driver.getHint().toString()+" Field Requires");
            Alert(AddTruckDetails.this,activityAddTruckDetailsBinding.driver.getHint().toString()+" Field Requires");
            return false;
        } {
            return true;
        }
    }

}