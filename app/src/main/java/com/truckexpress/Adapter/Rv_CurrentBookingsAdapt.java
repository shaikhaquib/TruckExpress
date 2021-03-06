package com.truckexpress.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Fragments.Frg_BookingDetails;
import com.truckexpress.Models.ModelCurrentBooking;
import com.truckexpress.Models.ModelTruck;
import com.truckexpress.R;
import com.truckexpress.databinding.ItemCurrentBookingsBinding;
import com.truckexpress.databinding.ManualTruckBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.entity.StringEntity;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Adapter.RV_ADHOCAdapter.displayExpense;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.AlertAutoLink;
import static com.truckexpress.Network.API.SaveManualTruck;
import static com.truckexpress.Network.API.TruckAssignedsave;
import static com.truckexpress.Network.API.TruckList;
import static com.truckexpress.Network.API.sumweight;
import static com.truckexpress.Network.API.truckcount;

public class Rv_CurrentBookingsAdapt extends RecyclerView.Adapter<Rv_CurrentBookingsAdapt.ViewHolder> implements Filterable {

    List<ModelCurrentBooking> currentBookings;
    Context context;
    Progress progress;
    private static final String TAG = "Rv_CurrentBookingsAdapt";
    List<TruckListJsonModel> truckListJsonModels = new ArrayList<>();
    List<ModelCurrentBooking> tempList;


    public Rv_CurrentBookingsAdapt(Context context, List<ModelCurrentBooking> currentBookings) {
        this.currentBookings = currentBookings;
        this.tempList = this.currentBookings;
        this.context = context;
        progress = new Progress(context);
    }

    public static void truckCount(Context context, int id, ModelCurrentBooking currentBooking, Progress progress, int flag, ItemCurrentBookingsBinding itemCurrentBookingsBinding) {
        if (progress != null)
            progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("bookingid", id);
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(context, truckcount, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (progress != null)
                    progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

                try {
                    JSONObject object = new JSONArray(result).getJSONObject(0);
                    int assigned = object.getInt("count");
                    int pending = currentBooking.getNooftrucks() - assigned;
                    String msg = "Assigned Trucks : " + assigned + "\nPending Trucks : " + pending;
                    if (flag == 1) {
                        AlertAutoLink(context, msg, "Truck Counts");
                    } else {
                        if (assigned > 0) {
                            itemCurrentBookingsBinding.bookingID.setBackgroundColor(Color.parseColor("#57BB8A"));
                        } else if (pending == 0) {
                            itemCurrentBookingsBinding.bookingID.setBackgroundColor(Color.parseColor("#00BCD4"));
                            itemCurrentBookingsBinding.Assign.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                if (progress != null)
                    progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ModelCurrentBooking modelLOT = currentBookings.get(position);
        holder.bindDATA(modelLOT);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    currentBookings = tempList;
                } else {
                    List<ModelCurrentBooking> filteredList = new ArrayList<>();
                    for (ModelCurrentBooking row : tempList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (String.valueOf(row.getBookingid()).toLowerCase().contains(charString.toLowerCase()) || row.getCompanyName().toLowerCase().contains(charString.toLowerCase()) || row.getTruckname().toLowerCase().contains(charString.toLowerCase()) || row.getSource().toLowerCase().contains(charString.toLowerCase()) || row.getDestination().toLowerCase().contains(charString.toLowerCase()) || String.valueOf(row.getGoodstype()).toLowerCase().contains(charString.toLowerCase())|| row.getPaymentname().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                            currentBookings = filteredList;
                        }
                    }

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filterResults;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                //  filterLists = (ArrayList<AssignConsumerItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    @Override
    public int getItemCount() {
        return currentBookings.size();
    }

    private void displayBidHistory(List<ModelTruck> bidLogs,ModelCurrentBooking currentBookings ,Context context) {
        final Dialog dialog  = new Dialog(context);
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


        if (bidLogs.isEmpty()) {
            dialog.findViewById(R.id.submit).setVisibility(View.GONE);
        }

        dialog.findViewById(R.id.manualTruck).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.manualTruck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                manualTruckForm(currentBookings.getBookingid(), currentBookings);
            }
        });

        dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (truckListJsonModels.size() > currentBookings.getNooftrucks()){
                    Alert(context,"You cannot select more than " +currentBookings.getNooftrucks()+" trucks.");
                }else if (truckListJsonModels.size() > 0){
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < truckListJsonModels.size(); i++) {
                        try {
                            JSONObject object = new JSONObject();
                            TruckListJsonModel truckListJsonModel = truckListJsonModels.get(i);
                            object.put("bookingid", truckListJsonModel.getBookingID());
                            object.put("truckid", truckListJsonModel.getTruckId());
                            object.put("transpoterid", truckListJsonModel.getTransPorterID());
                            object.put("trucknumber", truckListJsonModel.getTrucknumber());
                            object.put("truckweight", truckListJsonModel.getTruckweight());
                            object.put("rate", currentBookings.getRate());
                            object.put("rateunitid", currentBookings.getUnitid());
                            object.put("advance", currentBookings.getAdvance());
                            object.put("balance", currentBookings.getBalance());
                            object.put("truckavailibilty", truckListJsonModel.getTruckavailibilty());
                            if (currentBookings.getRate() != null)
                                object.put("freight", Constants.calculateFreight(currentBookings.getUnitid(), Double.parseDouble(currentBookings.getRate()), truckListJsonModel.getTruckweight()));
                            else
                                object.put("freight", Constants.calculateFreight(currentBookings.getUnitid(), 0, truckListJsonModel.getTruckweight()));

                            jsonArray.put(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "onClick: "+jsonArray.toString());
                    assignTruck(jsonArray,dialog);
                }else{
                    Alert(context,"Please Select Trucks");
                }

            }
        });

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
                ModelTruck bidLog = bidLogs.get(position);

                bidHolder.type.setText(bidLog.getTrucktype());
                bidHolder.number.setText(bidLog.getTruckname());
                bidHolder.number.setAllCaps(true);

                bidHolder.availibility.setText("select time");

                bidHolder.availibility.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog  = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.datepicker);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.show();

                        SingleDateAndTimePicker singleDateAndTimePicker = dialog.findViewById(R.id.singleDateAndTimePicker);
                        dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bidHolder.availibility.setText(new SimpleDateFormat("dd MMM yyyy HH:MM").format(singleDateAndTimePicker.getDate()));
                                dialog.dismiss();
                                bidHolder.isDateSelected = true;
                            }
                        });
                        dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });


                    bidHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (bidHolder.isDateSelected) {
                                if (isChecked) {
                                    truckListJsonModels.add(new TruckListJsonModel(bidLog.getTruckid(), currentBookings.getBookingid(), USERINFO.getId(), bidLog.getTruckname(), bidLog.getWeight(), bidHolder.availibility.getText().toString()));
                                } else {
                                    for (int i = 0; i < truckListJsonModels.size(); i++) {
                                        if (bidLog.getTruckid() == truckListJsonModels.get(i).getTruckId()) {
                                            truckListJsonModels.remove(i);
                                        }
                                    }
                                }
                            }else {
                                bidHolder.checkBox.setChecked(false);
                                Toast.makeText(context, "Please Select Availability Time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



            }

            @Override
            public int getItemCount() {
                return bidLogs.size();
            }

            class ListHolder extends RecyclerView.ViewHolder {
                TextView type, number, availibility;
                CheckBox checkBox;
                Boolean isDateSelected = false;

                public ListHolder(@NonNull View itemView) {
                    super(itemView);
                    type = itemView.findViewById(R.id.type);
                    number = itemView.findViewById(R.id.Number);
                    availibility = itemView.findViewById(R.id.Availibility);
                    checkBox = itemView.findViewById(R.id.checkbox);
                }
            }
        });
    }

    public static void sumWeight(Context context, Progress progress, ModelCurrentBooking currentBooking) {
        if (progress != null)
            progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("bookingid", currentBooking.getBookingid());
            jsonParams.put("userid", USERINFO.getId());
            entity = new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }

        client.post(context, sumweight, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (progress != null)
                    progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);

                try {
                    JSONObject object = new JSONArray(result).getJSONObject(0);
                    int assigned = object.getInt("totalweight");
                    int pending = currentBooking.getLotweight() - assigned;
                    String weightMsg = "Assigned Weight : " + assigned + " " + currentBooking.getUnitname() + "\nPending Weight : " + pending + " " + currentBooking.getUnitname();
                    AlertAutoLink(context, weightMsg, "Booking Details");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("xml", "Sending failed");
                if (progress != null)
                    progress.dismiss();
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml", "Progress : " + bytesWritten);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_current_bookings, parent, false));
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    private void getTrucks(ModelCurrentBooking currentBookings) {
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            jsonParams.put("trucktypeid", currentBookings.getTrucktypeid());
            jsonParams.put("tyerid", currentBookings.getTyerid());
            jsonParams.put("truckweightid", currentBookings.getTruckweightid());
            entity = new StringEntity(jsonParams.toString());

            Log.d(TAG, "getTrucks: " + jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context, TruckList, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: " + result);
                Object json = null;
                List<ModelTruck> modelTrucks = new ArrayList<>();

                try {
                    json = new JSONTokener(result).nextValue();

                    if (json instanceof JSONArray) {
                        JSONArray jsonArray = new JSONArray(result);

                        for (int y = 0; y < jsonArray.length(); y++) {
                            JSONObject object = jsonArray.getJSONObject(y);
                            Gson gson = new Gson();
                            ModelTruck modelState = gson.fromJson(object.toString(), ModelTruck.class);
                            modelTrucks.add(modelState);
                            if (modelState.getUIMessage().equals("No data found")) {
                                modelTrucks.clear();
                                Toast.makeText(context, "No Truck available for specified Truck Type", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayBidHistory(modelTrucks, currentBookings, context);

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

    private void manualTruckForm(int bookingid, ModelCurrentBooking currentBookings) {
        final ManualTruckBinding manualTruckBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.manual_truck, null, false);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(manualTruckBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
        dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        manualTruckBinding.truckavailibilty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.datepicker);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();

                manualTruckBinding.closeDiloge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                SingleDateAndTimePicker singleDateAndTimePicker = dialog.findViewById(R.id.singleDateAndTimePicker);
                dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        manualTruckBinding.truckavailibilty.setText(new SimpleDateFormat("dd MMM yyyy HH:MM").format(singleDateAndTimePicker.getDate()));
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.closeDiloge).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        manualTruckBinding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Pattern pattern1 = Pattern.compile("^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$");
                Matcher matcher1 = pattern1.matcher(manualTruckBinding.TruckNumber.getText().toString());
               if (manualTruckBinding.TruckNumber.getText().toString().isEmpty()) {
                    manualTruckBinding.TruckNumber.setError(manualTruckBinding.TruckNumber.getHint().toString() + " Field Requires");
                    Alert(context, manualTruckBinding.TruckNumber.getHint().toString() + " Field Requires");
                } else if (!matcher1.matches() && !manualTruckBinding.TruckNumber.getText().toString().toUpperCase().isEmpty()) {
                    manualTruckBinding.TruckNumber.setError("Please Enter Valid Truck no");
                    Alert(context, "Please Enter Valid Truck no");
                } else if (manualTruckBinding.TruckWeight.getText().toString().isEmpty()) {
                    manualTruckBinding.TruckWeight.setError(manualTruckBinding.TruckWeight.getHint().toString() + " Field Requires");
                    Alert(context, manualTruckBinding.TruckWeight.getHint().toString() + " Field Requires");
                } else if (manualTruckBinding.DriverName.getText().toString().isEmpty()) {
                    manualTruckBinding.DriverName.setError(manualTruckBinding.DriverName.getHint().toString() + " Field Requires");
                    Alert(context, manualTruckBinding.DriverName.getHint().toString() + " Field Requires");
                } else if (manualTruckBinding.DriverMobile.getText().toString().isEmpty()) {
                    manualTruckBinding.DriverMobile.setError(manualTruckBinding.DriverMobile.getHint().toString() + " Field Requires");
                    Alert(context, manualTruckBinding.DriverMobile.getHint().toString() + " Field Requires");
                } else if (manualTruckBinding.DriverMobile.getText().toString().length() < 10) {
                    manualTruckBinding.DriverMobile.setError("Please Enter Valid Phone no");
                    Alert(context, "Please Enter Valid Phone no");
                } else if (manualTruckBinding.truckavailibilty.getText().toString().isEmpty()) {
                    manualTruckBinding.truckavailibilty.setError(manualTruckBinding.truckavailibilty.getHint().toString() + " Field Requires");
                    Alert(context, manualTruckBinding.truckavailibilty.getHint().toString() + " Field Requires");
                } else {
                    AddManual(dialog, bookingid, manualTruckBinding.truckOwnerName.getText().toString(), manualTruckBinding.TruckNumber.getText().toString(),
                            manualTruckBinding.TruckWeight.getText().toString(), manualTruckBinding.DriverName.getText().toString(),
                            manualTruckBinding.DriverMobile.getText().toString(), manualTruckBinding.truckavailibilty.getText().toString(), currentBookings);
                }
            }
        });

    }

    private void AddManual(Dialog dialog, int bookingid, String truckOwnerName, String TruckNumber, String TruckWeight, String DriverName, String DriverMobile, String truckavailibilty, ModelCurrentBooking currentBookings) {
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("bookingid", bookingid);
            jsonParams.put("truckowner", truckOwnerName);
            jsonParams.put("transporterid", USERINFO.getId());
            jsonParams.put("trucknumber", TruckNumber);
            jsonParams.put("truckweight", TruckWeight);
            jsonParams.put("drivername", DriverName);
            jsonParams.put("mobileno", DriverMobile);
            jsonParams.put("truckavailibilty", truckavailibilty);
            jsonParams.put("rate", currentBookings.getRate());
            jsonParams.put("rateunitid", currentBookings.getUnitid());
            jsonParams.put("advance", currentBookings.getAdvance());
            jsonParams.put("balance", currentBookings.getBalance());
            if (currentBookings.getRate() != null)
                jsonParams.put("freight", Constants.calculateFreight(currentBookings.getUnitid(), Double.parseDouble(currentBookings.getRate()), Double.parseDouble(TruckWeight)));
            else
                jsonParams.put("freight", Constants.calculateFreight(currentBookings.getUnitid(), 0, Double.parseDouble(TruckWeight)));

            entity = new StringEntity(jsonParams.toString());

            Log.d(TAG, "getTrucks: " + jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context,SaveManualTruck,entity,"application/json" , new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);
                if (result.equals("\"success\"")) {
                    dialog.dismiss();
                    Alert(context, "Added Successfully");

                } else {
                    Alert(context, "Some thing went wrong..");
                }

            }

            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                String result = new String(bytes);
                Log.i("xml",result);
                progress.dismiss();
                Alert(context, "Some thing went wrong..");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.i("xml","Progress : "+bytesWritten);
            }
        });
    }

    private void assignTruck(JSONArray jsonArray, Dialog dialog) {
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);

        StringEntity entity = null;
        try {
            entity = new StringEntity(jsonArray.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context,TruckAssignedsave,entity,"application/json" ,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);

                if (result.equals("\"success\"")) {
                    dialog.dismiss();
                    Alert(context,"Assigned Successfully");
                }else {
                    Alert(context,result);

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

    class TruckListJsonModel{


        int TruckId;
        int BookingID;
        int TransPorterID;
        String trucknumber;
        int truckweight;
        String truckavailibilty;

        public TruckListJsonModel(int truckId, int bookingID, int transPorterID, String trucknumber, String truckweight, String truckavailibilty) {
            TruckId = truckId;
            BookingID = bookingID;
            TransPorterID = transPorterID;
            this.trucknumber = trucknumber;
            this.truckweight = Integer.parseInt(truckweight.trim());
            this.truckavailibilty = truckavailibilty;
        }

        public String getTrucknumber() {
            return trucknumber;
        }

        public int getTruckweight() {
            return truckweight;
        }

        public String getTruckavailibilty() {
            return truckavailibilty;
        }



        public int getTruckId() {
            return TruckId;
        }

        public int getBookingID() {
            return BookingID;
        }

        public int getTransPorterID() {
            return TransPorterID;
        }


    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemCurrentBookingsBinding itemLotBinding;

        public ViewHolder(ItemCurrentBookingsBinding binding) {
            super(binding.getRoot());
            this.itemLotBinding = binding;
        }

        private void bindDATA(final ModelCurrentBooking modelLOT) {
            itemLotBinding.bookingID.setText("Booking ID :" + modelLOT.getBookingid());
            itemLotBinding.corporateName.setText(Constants.capitalize(modelLOT.getCompanyName()));


            // Do something after 5s = 5000ms
            //        new TruckCountForDetails(context, modelLOT.getBookingid(), modelLOT, null, 0, itemLotBinding).execute();

            int assigned = modelLOT.getCount();
            int pending = modelLOT.getNooftrucks() - assigned;
            if (assigned > 0) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#57BB8A"));
            }
            if (pending == 0) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#00BCD4"));
                itemLotBinding.Assign.setVisibility(View.GONE);
            }

            if (modelLOT.getWeight().isEmpty() || modelLOT.getWeight() == null) {
                itemLotBinding.weight.setText("No Data");
            } else {
                itemLotBinding.weight.setText(modelLOT.getWeight() + "Ton");
            }

            itemLotBinding.Amount.setText(modelLOT.getRate() + " " + modelLOT.getUnitname());
            itemLotBinding.trckType.setText(modelLOT.getTrucktypename() + "\n/" + modelLOT.getNooftyres() + " tyre");
            itemLotBinding.source.setText(modelLOT.getSource());
            itemLotBinding.destination.setText(modelLOT.getDestination());
            itemLotBinding.pickuplocation.setText(modelLOT.getPickupaddress());
            itemLotBinding.dropLocation.setText(modelLOT.getDropaddress());

            itemLotBinding.goodsType.setText(modelLOT.getGoodsname() + " " + modelLOT.getShrotageallowance());
            itemLotBinding.paymentmode.setText(modelLOT.getPaymentname());
            itemLotBinding.totalfreight.setVisibility(View.GONE);
            itemLotBinding.expense.setText(String.valueOf(modelLOT.getTotalfreight()));
            itemLotBinding.checkList.setText("Checklist : " + modelLOT.getChecklistcount());
            itemLotBinding.expenseTotal.setText("Expense : " + modelLOT.getTotalexpenses());


            itemLotBinding.paymentmode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelLOT.getPaymentmode() == 1) {
                        String msg = "Advance : " + modelLOT.getAdvance() + " %" + "\n" +
                                "Balance : " + modelLOT.getBalance();
                        AlertAutoLink(context, msg, "Payments Details");
                    } else if (modelLOT.getPaymentmode() == 4) {
                        String msg = "Advance : " + modelLOT.getAdvance() + " %" + "\n" +
                                "Balance : " + modelLOT.getBalance() + "\n" +
                                "No Of Days : " + modelLOT.getNoofdays();
                        AlertAutoLink(context, msg, "Payments Details");
                    } else if (modelLOT.getPaymentmode() == 3) {
                        String msg =
                                "No Of Days : " + modelLOT.getNoofdays();
                        AlertAutoLink(context, msg, "Payments Details");
                    }
                }
            });

            itemLotBinding.expenseTotal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayExpense(context, String.valueOf(modelLOT.getBookingid()));
                }
            });
            itemLotBinding.Amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RV_ADHOCAdapter.BidHistory(progress, context).execute(String.valueOf(modelLOT.getBookingid()), String.valueOf(modelLOT.getCorporateid()));
                }
            });
            itemLotBinding.checkList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RV_ADHOCAdapter.GetChecklist(new Progress(context), context).execute(String.valueOf(modelLOT.getBookingid()));
                }
            });

            itemLotBinding.Assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTrucks(modelLOT);
                }
            });
            itemLotBinding.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Frg_BookingDetails newFragment = new Frg_BookingDetails();
                    Bundle args = new Bundle();
                    args.putSerializable("itemBooking", modelLOT);
                    newFragment.setArguments(args);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();

                }
            });

            if (modelLOT.getBookingtype().equals("1")) {
                itemLotBinding.weight.setVisibility(View.GONE);
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#bbe1fa"));
                itemLotBinding.noofTruck.setText("Weight : " + modelLOT.getNooftrucks());
                itemLotBinding.noofTruck.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_weighing_machine, 0, 0, 0);
                itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate() + " to " + modelLOT.getTodate());

            } else {
                itemLotBinding.noofTruck.setText("Number : " + modelLOT.getNooftrucks());
                itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate());
            }

            itemLotBinding.noofTruck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelLOT.getBookingtype().equals("1")) {
                        sumWeight(context, progress, modelLOT);
                    } else {
                        truckCount(context, modelLOT.getBookingid(), modelLOT, new Progress(context), 1, null);
                    }
                }
            });


            itemLotBinding.corporateName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = "Name : " + modelLOT.getName() + "\n" + "Email : " + modelLOT.getCorporateContactEmail() + "\n" +
                            "Mobile No : " + modelLOT.getCorporateContactPerson();
                    AlertAutoLink(context, msg, "Corporate Details");
                }
            });

            itemLotBinding.Showmore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        itemLotBinding.viewMore.setVisibility(View.VISIBLE);
                        itemLotBinding.Showmore.setText("Show Less");
                    } else {
                        itemLotBinding.viewMore.setVisibility(View.GONE);
                        itemLotBinding.Showmore.setText("Show More");
                    }
                }
            });

        }

    }


}
