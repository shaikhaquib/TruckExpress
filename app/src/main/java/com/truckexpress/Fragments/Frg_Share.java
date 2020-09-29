package com.truckexpress.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abdeveloper.library.MultiSelectDialog;
import com.abdeveloper.library.MultiSelectModel;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelChecklist;
import com.truckexpress.Models.ModelExpence;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.Models.ModelMotorOwner;
import com.truckexpress.Models.ModelUnit;
import com.truckexpress.R;
import com.truckexpress.databinding.FrgSharedetailsBinding;

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
import static com.truckexpress.Extras.Constants.AlertAutoLink;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.Bookingshareexpensesfinalinsert;
import static com.truckexpress.Network.API.Bookingshareinsert;
import static com.truckexpress.Network.API.Checklistshow;
import static com.truckexpress.Network.API.ExpensesDatashow;
import static com.truckexpress.Network.API.RouteMotorOwnerList;
import static com.truckexpress.Network.API.UnitsList;

public class Frg_Share extends DialogFragment {
    private static final String TAG = "Frg_Share";
    Bundle bundle;
    FrgSharedetailsBinding binding;
    ModelLOT modelLOT;
    Context context;
    List<ModelChecklist> modelChecklist = new ArrayList<>();
    List<ModelMotorOwner> motorOwners = new ArrayList<>();
    List<ModelUnit> modelUnits = new ArrayList<>();
    int unitID;
    List<String> mstringBuilder = new ArrayList<>();
    List<String> mchecklistID = new ArrayList<>();
    String moterOwnerIds;
    List<String> stringBuilder = new ArrayList<>();
    List<String> checklistID = new ArrayList<>();
    String stringIds;
    Boolean isFinalRequest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.frg_sharedetails, container, false);
        bundle = getArguments();
        if (bundle != null)
            modelLOT = (ModelLOT) bundle.getSerializable("itemBooking");
        context = getActivity();
        getCheckList();
        getMotorOwner();
        getUnits();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        binding.customerNo.setText(Constants.capitalize(modelLOT.getCompanyName()));
        binding.pickUPdate.setText(modelLOT.getPickupdate());
        binding.rate.setText(modelLOT.getRate());
        binding.unit.setText(modelLOT.getUnitid());
        binding.truckType.setText(modelLOT.getTrucktype() + "\n/" + modelLOT.getTyre() + " tyre");
        binding.Source.setText(modelLOT.getSource());
        binding.destination.setText(modelLOT.getDestination());
        binding.pickuplocation.setText(modelLOT.getPickupaddress());
        binding.dropLocation.setText(modelLOT.getDropaddress());
        binding.expense.setText("₹ " + modelLOT.getTotalexpenses());

        binding.goodsType.setText(modelLOT.getGoodstype() + " " + modelLOT.getShortageallowance());
        binding.noofTruck.setText(String.valueOf(modelLOT.getBookingnooftruck()));

        binding.addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayExpense(context, String.valueOf(modelLOT.getId()));
            }
        });


        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.rate.getText().toString().isEmpty()) {
                    binding.rate.setError("Field Required...");
                    Alert(context, "Please set Rate");
                } else if (Double.parseDouble(binding.rate.getText().toString()) <= 0.0) {
                    binding.rate.setError("Please Enter Valid data ...");
                    Alert(context, "Please Enter Valid data ...");
                } else if (binding.noofTruck.getText().toString().isEmpty()) {
                    binding.noofTruck.setError("Field Required...");
                    Alert(context, "Please set Number of Truck");
                } else if (modelLOT.getBookingnooftruck() < Integer.parseInt(binding.noofTruck.getText().toString().trim())) {
                    binding.noofTruck.setError("Please Enter Valid data ...");
                    Alert(context, "Please Enter Valid data ...");
                } else if (moterOwnerIds == null) {
                    binding.myMotorOwner.setError("Please Select Motor Owner to share ...");
                    Alert(context, "Please Select Motor Owner to share ...");
                } else {
                    new ShareBooking().execute(binding.rate.getText().toString(), binding.noofTruck.getText().toString());
                }
            }
        });


        binding.customerNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = "Name : " + modelLOT.getName() + "\n" +
                        "Email ID : " + modelLOT.getCorporateContactPersoneEmail() + "\n" +
                        "Mobile No : " + modelLOT.getCorporateContactPerson();
                final SpannableString s = new SpannableString(msg); // msg should have url to enable clicking
                Linkify.addLinks(s, Linkify.ALL);

                AlertAutoLink(context, s.toString(), "Corporate Details");
            }
        });

        binding.checkList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MultiSelectModel> driversName = new ArrayList<>();
                for (int i = 0; i < modelChecklist.size(); i++) {
                    ModelChecklist checklist = modelChecklist.get(i);
                    driversName.add(new MultiSelectModel(checklist.getId(), checklist.getCheklist()));
                }

                new MultiSelectDialog()
                        .title("Select Checklist")
                        .positiveText("Done")
                        .multiSelectList(driversName)
                        .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                            @Override
                            public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String s) {
                                for (int i = 0; i < selectedIds.size(); i++) {
                                    stringBuilder.add(selectedNames.get(i));
                                    checklistID.add(String.valueOf(selectedIds.get(i)));
                                }
                                binding.checkList.setText(TextUtils.join(",", stringBuilder.toArray()));
                                stringIds = TextUtils.join(",", checklistID.toArray());
                                stringBuilder.clear();
                                checklistID.clear();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show(getActivity().getSupportFragmentManager(), TAG);
            }
        });
        binding.myMotorOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MultiSelectModel> driversName = new ArrayList<>();
                for (int i = 0; i < motorOwners.size(); i++) {
                    ModelMotorOwner checklist = motorOwners.get(i);
                    Log.d(TAG, "onClick: " + checklist.getId());
                    driversName.add(new MultiSelectModel(checklist.getMotorownerid(), checklist.getFirstname() + " " + checklist.getLastname()));
                }

                new MultiSelectDialog()
                        .title("Select Motor Owners")
                        .positiveText("Done")
                        .multiSelectList(driversName)
                        .onSubmit(new MultiSelectDialog.SubmitCallbackListener() {
                            @Override
                            public void onSelected(ArrayList<Integer> selectedIds, ArrayList<String> selectedNames, String s) {
                                for (int i = 0; i < selectedIds.size(); i++) {
                                    mstringBuilder.add(selectedNames.get(i));
                                    mchecklistID.add(String.valueOf(selectedIds.get(i)));
                                }
                                binding.myMotorOwner.setText(TextUtils.join(",", mstringBuilder.toArray()));
                                moterOwnerIds = TextUtils.join(",", mchecklistID.toArray());
                                mstringBuilder.clear();
                                mchecklistID.clear();
                            }

                            @Override
                            public void onCancel() {

                            }
                        }).show(getActivity().getSupportFragmentManager(), TAG);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    private void getCheckList() {
        Progress progress = new Progress(getActivity());
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

        client.post(getActivity(), Checklistshow, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

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
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    private void getMotorOwner() {
        final Progress progress = new Progress(getActivity());
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            jsonParams.put("source", modelLOT.getSource());
            jsonParams.put("destination", modelLOT.getDestination());
            entity = new StringEntity(jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getActivity(), RouteMotorOwnerList, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

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
                Log.i("xml", "Sending failed");
                String result = new String(bytes);
                Log.d(TAG, "onSuccess: " + result);
                progress.dismiss();
            }

        });
    }


    public void displayExpense(Context context, String id) {
        Progress progress = new Progress(context);
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("bookingid", id);
            entity = new StringEntity(jsonParams.toString());

            Log.d(TAG, "getTrucks: " + jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context, ExpensesDatashow, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);
                Object json = null;
                List<ModelExpence> modelExpences = new ArrayList<>();

                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {
                        JSONArray jsonArray = new JSONArray(result);

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelExpence modelExpence = gson.fromJson(object.toString(), ModelExpence.class);
                            modelExpences.add(modelExpence);
                        }


                        final Dialog dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.dialog_lis_truck);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.show();
                        RecyclerView rvLOT = dialog.findViewById(R.id.list);
                        rvLOT.setLayoutManager(new LinearLayoutManager(context));
                        rvLOT.hasFixedSize();
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

                        TextView TruckNo, TruckTyre, truckType, materialTextView;
                        TruckNo = dialog.findViewById(R.id.TruckNo);
                        TruckTyre = dialog.findViewById(R.id.TruckTyre);
                        truckType = dialog.findViewById(R.id.truckType);
                        materialTextView = dialog.findViewById(R.id.materialTextView);
                        AutoCompleteTextView autoCompleteTextView = dialog.findViewById(R.id.unitDrop);
                        dialog.findViewById(R.id.viewAddExpense).setVisibility(View.VISIBLE);
                        EditText name, rate;
                        name = dialog.findViewById(R.id.name);
                        rate = dialog.findViewById(R.id.rate);

                        dialog.findViewById(R.id.addExpense).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (name.getText().toString().isEmpty()) {
                                    name.setError("Field required");
                                    Toast.makeText(context, "Name Field Required", Toast.LENGTH_SHORT).show();
                                } else if (rate.getText().toString().isEmpty()) {
                                    rate.setError("Field required");
                                    Toast.makeText(context, "Rate Field Required", Toast.LENGTH_SHORT).show();
                                } else if (autoCompleteTextView.getText().toString().isEmpty()) {
                                    autoCompleteTextView.setError("Select Unit First");
                                    Toast.makeText(context, "Select Unit First", Toast.LENGTH_SHORT).show();
                                } else {

                                    Double lgRate = Double.parseDouble(rate.getText().toString());
                                    Double amount;
                                    if (unitID == 1) {
                                        amount = lgRate * Double.parseDouble(modelLOT.getWeight());
                                    } else if (unitID == 2) {
                                        amount = lgRate * 1000 * Double.parseDouble(modelLOT.getWeight());
                                    } else if (unitID == 3) {
                                        amount = lgRate * 10 * Double.parseDouble(modelLOT.getWeight());
                                    } else {
                                        amount = lgRate;
                                    }

                                    expensesfinalinsert(modelExpences, rvLOT, name.getText().toString(), Double.parseDouble(rate.getText().toString()), String.valueOf(unitID), amount, String.valueOf(modelLOT.getBookingid()));
                                }
                            }
                        });

                        List<String> states = new ArrayList<>();
                        for (int j = 0; j < modelUnits.size(); j++) {
                            ModelUnit modelState = modelUnits.get(j);
                            states.add(modelState.getUnit());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                                R.layout.textview,
                                states.toArray(new String[states.size()]));
                        autoCompleteTextView.setAdapter(adapter);

                        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                unitID = modelUnits.get(position).getId();
                            }
                        });

                        materialTextView.setText("Expense List");
                        truckType.setText("Name");
                        TruckTyre.setText("Rate");
                        TruckNo.setText("Unit");

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
                                ListHolder bidHolder = (ListHolder) holder;
                                ModelExpence bidLog = modelExpences.get(position);

                                bidHolder.type.setText(bidLog.getName());
                                bidHolder.number.setText(bidLog.getRate());
                                bidHolder.availibility.setText(bidLog.getUnit());
                                //bidHolder.availibility.setAllCaps(true);
                            }

                            @Override
                            public int getItemCount() {
                                return modelExpences.size();
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    private void getUnits() {
        final Progress progress = new Progress(getActivity());
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(20 * 1000);
        client.post(UnitsList, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

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


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }


    private void expensesfinalinsert(List<ModelExpence> modelExpences, RecyclerView rvLOT, String name, Double rate, String unitId, Double amount, String bookingID) {
        final Progress progress = new Progress(getActivity());
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {
            JSONArray jsonArray = new JSONArray();

            JSONObject jsonParams = new JSONObject();
            jsonParams.put("name", name);
            jsonParams.put("rate", rate);
            jsonParams.put("unitid", unitId);
            jsonParams.put("userid", USERINFO.getId());
            jsonParams.put("amount", amount);
            jsonParams.put("bookingid", bookingID);

            jsonArray.put(jsonParams);
            entity = new StringEntity(jsonArray.toString());

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(getActivity(), Bookingshareexpensesfinalinsert, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

                if (result.equals("\"success\"")) {
                    modelLOT.setTotalexpenses(modelLOT.getTotalexpenses() + (int) Math.round(amount));
                    binding.expense.setText("" + modelLOT.getTotalexpenses());
                    ModelExpence modelExpense = new ModelExpence();
                    modelExpense.setRate(String.valueOf(rate));
                    modelExpense.setName(name);
                    for (int j = 0; j < modelUnits.size(); j++) {
                        if (modelUnits.get(j).getId() == unitID) {
                            modelExpense.setUnit(modelUnits.get(j).getUnit());
                        }
                    }
                    modelExpense.setRate("" + rate);

                    modelExpences.add(modelExpense);
                    rvLOT.getAdapter().notifyDataSetChanged();
                } else {
                    Alert(getActivity(), result);
                }

                Object json = null;

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    public class ShareBooking extends AsyncTask<String, Void, String> {
        HttpURLConnection conn;
        URL url = null;
        Context context = getActivity();
        Progress progress = new Progress(getActivity());

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(Bookingshareinsert);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject object = new JSONObject();
                object.put("bookingid", String.valueOf(modelLOT.getId()));
                object.put("rate", params[0]);
                object.put("nooftruck", params[1]);
                object.put("expenseslist", modelLOT.getTotalexpenses());
                object.put("checklist", stringIds);
                object.put("transpoterid", String.valueOf(USERINFO.getId()));
                object.put("motorowner", "[" + moterOwnerIds + "]");
                Log.d("TAG", "doInBackground: " + object.toString());

                OutputStream os = conn.getOutputStream();
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
                    return (result.toString());


                } else {

                    return ("unsuccessful");
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
            Log.d(TAG, "onPostExecute: " + s);

            if (s.equals("\"success\"")) {
                Alert(context, "You have Successfully shared this booking");
            } else {
                Alert(context, "Some thing went wrong..");
            }

        }
    }

}
