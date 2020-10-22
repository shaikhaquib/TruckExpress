package com.truckexpress.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.MyItemDecoration;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Fragments.Frg_Share;
import com.truckexpress.Models.BIDLog;
import com.truckexpress.Models.ModelBidDetails;
import com.truckexpress.Models.ModelExpence;
import com.truckexpress.Models.ModelLOT;
import com.truckexpress.R;
import com.truckexpress.databinding.AccepAlertBinding;
import com.truckexpress.databinding.AddBidBinding;
import com.truckexpress.databinding.ItemAdhocBinding;

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
import static com.truckexpress.Network.API.Bid_Acceptedbytranspoter;
import static com.truckexpress.Network.API.Bidshowlog;
import static com.truckexpress.Network.API.BookingReasonInsert;
import static com.truckexpress.Network.API.CHECKLIST;
import static com.truckexpress.Network.API.CORPORATEBID;
import static com.truckexpress.Network.API.ExpensesDatashow;
import static com.truckexpress.Network.API.SAVEBID;

public class RV_ADHOCAdapter extends RecyclerView.Adapter<RV_ADHOCAdapter.ViewHolder> implements Filterable {

    List<ModelLOT> modelLOTS;
    List<ModelLOT> tempList;
    Context context;
    Progress progress;
    private static final String TAG = "RV_ADHOCAdapter";



    public RV_ADHOCAdapter(Context context, List<ModelLOT> modelLOTS) {
        this.modelLOTS = modelLOTS;
        this.tempList = modelLOTS;
        this.context = context;
        progress = new Progress(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_adhoc, parent, false));
        viewHolder.setIsRecyclable(false);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ModelLOT modelLOT = modelLOTS.get(position);
        holder.bindDATA(modelLOT);
    }


    @Override
    public int getItemCount() {
        return modelLOTS.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    modelLOTS = tempList;
                } else {
                    List<ModelLOT> filteredList = new ArrayList<>();
                    for (ModelLOT row : tempList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (String.valueOf(row.getBookingid()).toLowerCase().contains(charString.toLowerCase()) || row.getCompanyName().toLowerCase().contains(charString.toLowerCase()) || row.getTrucktype().toLowerCase().contains(charString.toLowerCase()) || row.getSource().toLowerCase().contains(charString.toLowerCase()) || row.getDestination().toLowerCase().contains(charString.toLowerCase()) || row.getGoodstype().toLowerCase().contains(charString.toLowerCase()) || row.getPaymentname().toLowerCase().contains(charString.toLowerCase()) || row.getWeight().toLowerCase().contains(charString.toLowerCase()) || row.getPickupaddress().toLowerCase().contains(charString.toLowerCase()) || row.getDropaddress().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    modelLOTS = filteredList;
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

    private void addBIDDialoge(final ModelLOT modelLOT) {
        final AddBidBinding bidBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.add_bid, null, false);

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(bidBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        bidBinding.closeDiloge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bidBinding.bookingID.setText("Booking ID :" + modelLOT.getId());
        bidBinding.noofTruck.setText("Number : " + modelLOT.getBookingnooftruck());
        bidBinding.trckType.setText(modelLOT.getTrucktype() + " / " + modelLOT.getTyre() + "tyre");
        bidBinding.rate.setText(String.valueOf(modelLOT.getRate()).trim());
        bidBinding.unit.setText(modelLOT.getUnitid());
        bidBinding.edtNoofTruck.setText("" + modelLOT.getBookingnooftruck());

        bidBinding.saveBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidBinding.rate.getText().toString().isEmpty()) {
                    bidBinding.rate.setError("Field Required...");
                    Alert(context, "Please set Rate");
                } else if (Double.parseDouble(bidBinding.rate.getText().toString()) <= 0.0) {
                    bidBinding.rate.setError("Please Enter Valid data ...");
                    Alert(context, "Please Enter Valid data ...");
                } else if (bidBinding.edtNoofTruck.getText().toString().isEmpty()) {
                    bidBinding.edtNoofTruck.setError("Field Required...");
                    Alert(context, "Please set Number of Truck");
                } else if (modelLOT.getBookingnooftruck() < Integer.parseInt(bidBinding.edtNoofTruck.getText().toString().trim())) {
                    bidBinding.edtNoofTruck.setError("Please Enter Valid data ...");
                    Alert(context, "Please Enter Valid data ...");
                } else {
                    new SaveBID(modelLOT, dialog, progress, context).execute(bidBinding.rate.getText().toString(), bidBinding.edtNoofTruck.getText().toString());
                }
            }
        });


    }
    private static void acceptBIDDialoge(final ModelBidDetails modelLOT, final ModelLOT lot, Context context,int flag,MaterialTextView materialTextView) {
        final AccepAlertBinding bidBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout. accep_alert, null, false);

        final Dialog dialog  = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(bidBinding.getRoot());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        bidBinding.closeDiloge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        bidBinding.bookingID.setText("Booking ID :"+modelLOT.getBookingid());

        if (flag == 1)
            bidBinding.noofTruck.setText("Number : "+modelLOT.getNooftrucks());
        else{
            bidBinding.noofTruck.setText(modelLOT.getNooftrucks()+" "+modelLOT.getUnitname());
            Drawable img = context.getResources().getDrawable(R.drawable.ic_weighing_machine);
            bidBinding.noofTruck.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        bidBinding.rate.setText(String.valueOf(modelLOT.getRate()).trim());
        bidBinding.unit.setText(""+modelLOT.getUnitname());
        bidBinding.source.setText(lot.getSource());
        bidBinding.destination.setText(lot.getDestination());
        bidBinding.pickuplocation.setText(lot.getPickupaddress());
        bidBinding.dropLocation.setText(lot.getDropaddress());

        bidBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               new AcceptBiD(context,modelLOT,materialTextView).execute(String.valueOf(lot.getTransporterid()));
            }
        });
    }

    public static class SaveBID extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        ModelLOT modelLOT;
        Dialog dialog;
        Progress progress;
        Context context;

        public SaveBID(ModelLOT modelLOT, Dialog dialog,Progress progress, Context context) {
            this.modelLOT = modelLOT;
            this.dialog = dialog;
            this.progress = progress;
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(SAVEBID);
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
                object.put("bookingid",String.valueOf(modelLOT.getId()));
                object.put("transpoterid",String.valueOf(USERINFO.getId()));
                object.put("corporateid",String.valueOf(modelLOT.getCorporateid()));
                object.put("trucktype",modelLOT.getTrucktype());
                object.put("rate",params[0]);
                object.put("unitid",String.valueOf(modelLOT.getUnitId()));
                object.put("motorowner","");
                object.put("nooftruck",params[1]);
                Log.d("TAG", "doInBackground: "+object.toString());

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

            if (s.equals("\"success\"")) {
                dialog.dismiss();
                Alert(context, "We have registered your BID");
            } else {
                Alert(context, "Some thing went wrong..");
            }

        }
    }

    public static class BidDetails extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        ModelLOT modelLOT;
        Dialog dialog;
        Progress progress;
        Context context;
        MaterialTextView materialTextView;
        int flag;

        public BidDetails(Progress progress, ModelLOT modelLOT, Context context, int i, MaterialTextView materialTextView) {
            this.progress = progress;
            this.modelLOT = modelLOT;
            this.context = context;
            this.materialTextView = materialTextView;
            flag = i;
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(CORPORATEBID);
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
                object.put("bookingid",params[0]);
                object.put("transporterid",String.valueOf(USERINFO.getId()));
                object.put("corporateid",params[1]);

                Log.d("TAG", "doInBackground: "+object.toString());

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


            if (!s.equals("unsuccessful")) {


                Log.d(TAG, "onResponse: " + s);

                Object json = null;
                try {
                    json = new JSONTokener(s).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(s);

                        try {
                            JSONObject object = jsonArray.getJSONObject(0);
                            Gson gson = new Gson();
                            ModelBidDetails bidDetails = gson.fromJson(object.toString(), ModelBidDetails.class);
                            acceptBIDDialoge(bidDetails,modelLOT,context,flag,materialTextView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }else   {
                        Alert(context, s);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                Alert(context, "Some thing went wrong..");
            }

        }
    }

    public static class GetChecklist extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        Progress progress;
        Context context;

        public GetChecklist(Progress progress, Context context) {
            this.progress = progress;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(CHECKLIST);
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
                object.put("id",params[0]);

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


            if (!s.equals("unsuccessful")) {
                Object json = null;
                List<String> strings = new ArrayList<>();
                try {
                    json = new JSONTokener(s).nextValue();

                    if (json instanceof JSONArray) {
                        JSONArray jsonArray = new JSONArray(s);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            strings.add(object.getString("cheklist"));
                        }
                    }else {
                        JSONObject object = new JSONObject(s);
                        strings.add(object.getString("cheklist"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                showChecklist(strings,context);
            } else {
                Alert(context, "Some thing went wrong..");
            }

        }
    }

    public static class BidHistory extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        Progress progress;
        Context context;

        public BidHistory(Progress progress, Context context) {
            this.progress = progress;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(Bidshowlog);
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
                object.put("bookingid",params[0]);
                object.put("transporterid",USERINFO.getId());
                object.put("corporateid",params[1]);

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


            if (!s.equals("unsuccessful")) {
                Object json = null;
                List<BIDLog> strings = new ArrayList<>();
                try {
                    json = new JSONTokener(s).nextValue();

                    if (json instanceof JSONArray) {
                        JSONArray jsonArray = new JSONArray(s);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Gson gson = new Gson();
                            BIDLog bidLog = gson.fromJson(object.toString(), BIDLog.class);
                            strings.add(bidLog);
                        }
                    }else {
                        JSONObject object = new JSONObject(s);
                        Gson gson = new Gson();
                        BIDLog bidLog = gson.fromJson(object.toString(), BIDLog.class);
                        strings.add(bidLog);                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                displayBidHistory(strings,context);
            } else {
                Alert(context, "Some thing went wrong..");
            }

        }

        private void displayBidHistory(List<BIDLog> bidLogs, Context context) {
            final Dialog dialog  = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_lis);
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
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bid_log, parent, false);
                    return new BIDHolder(v);
                }

                @Override
                public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                    BIDHolder bidHolder = (BIDHolder)holder;
                    BIDLog bidLog = bidLogs.get(position);
                    bidHolder.title.setText("Rate : ₹" + bidLog.getRate() + " " + bidLog.getUnitname());
                    bidHolder.desc.setText("No of Trucks :" + bidLog.getNooftrucks());

                    if (bidLog.getAcceptedbycorporate() == 1 && bidLog.getAcceptedbytransporter() ==1){
                        bidHolder.materialCardView.setCardBackgroundColor(Color.parseColor("#76E8D9"));
                    }else if (bidLog.getAcceptedbycorporate() == 1){
                        bidHolder.materialCardView.setCardBackgroundColor(Color.parseColor("#FFFFE974"));
                    }

                    if (bidLog.getMsg().equals("TE")) {

                        bidHolder.tranView.setVisibility(View.VISIBLE);
                        bidHolder.CoorporatorView.setVisibility(View.GONE);
                        bidHolder.Transtitle.setText("Rate : ₹" + bidLog.getRate() + " " + bidLog.getUnitname());
                        bidHolder.Transdesc.setText("No of Trucks :" + bidLog.getNooftrucks());
                        bidHolder.TransbidBY.setText("Bid :Transporter");

                    } else if (bidLog.getMsg().equals("Corporate")) {
                        bidHolder.bidBY.setText("Bid :Corporate");
                    } else if (bidLog.getMsg().equals("Accept"))
                        bidHolder.bidBY.setText("Bid :Accepted");

                }

                @Override
                public int getItemCount() {
                    return bidLogs.size();
                }
            });
        }
    }

    private static class AcceptBiD extends AsyncTask<String , Void ,String>{
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        ModelBidDetails modelLOT;
        Dialog dialog;
        Progress progress;
        Context context;
        MaterialTextView materialTextView;

        public AcceptBiD(Context context, ModelBidDetails modelBidDetails,MaterialTextView materialTextView) {
            this.modelLOT = modelBidDetails;
            this.context = context;
            this.materialTextView = materialTextView;
            progress = new Progress(context);
        }

        @Override
        protected void onPreExecute() {
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                url = new URL(Bid_Acceptedbytranspoter);
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
                object.put("bookingid",String.valueOf(modelLOT.getBookingid()));
                object.put("transpoterid",String.valueOf(USERINFO.getId()));
                object.put("corporateid",String.valueOf(modelLOT.getCorporateid()));
                object.put("trucktype",params[0]);
                object.put("rate",modelLOT.getRate());
                object.put("unitid",String.valueOf(modelLOT.getUnitid()));
                object.put("nooftruck",modelLOT.getNooftrucks());
                Log.d("TAG", "doInBackground: "+object.toString());

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

            if (s.equals("\"success\"")) {
                if (modelLOT.getAcceptedbycorporate() == 1) {
                    Alert(context, "CONGRATULATION!!! YOUR BID HAS BEEN ACCEPTED.");
                } else {
                    Alert(context, "We have registered your request");
                }
                materialTextView.setBackgroundColor(Color.parseColor("#FFFFE974"));
            } else {
                Alert(context, "Some thing went wrong..");
            }

        }
    }

    public static void showChecklist(List<String> strings, Context context) {
        if (strings.size() > 0) {
            String[] checkList = new String[strings.size()];
            checkList = strings.toArray(checkList);
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Checklist")
                    .setItems(checkList, null)
                    .setPositiveButton("Ok", null).show();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemAdhocBinding itemLotBinding;

        public ViewHolder(ItemAdhocBinding binding) {
            super(binding.getRoot());
            this.itemLotBinding = binding;
        }

        private void bindDATA(final ModelLOT modelLOT) {
            itemLotBinding.bookingID.setText("Booking ID :" + modelLOT.getId());
            itemLotBinding.corporateName.setText(Constants.capitalize(modelLOT.getCompanyName()));
            itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate());
            itemLotBinding.Amount.setText(modelLOT.getRate() + " " + modelLOT.getUnitid());
            itemLotBinding.trckType.setText(modelLOT.getTrucktype() + "\n/" + modelLOT.getTyre() + " tyre");
            itemLotBinding.source.setText(modelLOT.getSource());
            itemLotBinding.destination.setText(modelLOT.getDestination());
            itemLotBinding.pickuplocation.setText(modelLOT.getPickupaddress());
            itemLotBinding.dropLocation.setText(modelLOT.getDropaddress());

            itemLotBinding.goodsType.setText(modelLOT.getGoodstype() + " " + modelLOT.getShortageallowance());
            itemLotBinding.paymentmode.setText(modelLOT.getPaymentname());
            itemLotBinding.totalfreight.setText("₹ " + modelLOT.getTotalfreight());
            itemLotBinding.expense.setText("₹ " + modelLOT.getTotalexpenses() + "(Per Truck)");
            itemLotBinding.noofTruck.setText("Number : " + modelLOT.getNooftrucks());
            itemLotBinding.checkList.setText("Checklist : " + modelLOT.getChecklistcount());

            itemLotBinding.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.dialog_remark);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    dialog.getWindow().setAttributes(lp);
                    dialog.show();
                    TextInputEditText remark = dialog.findViewById(R.id.remark);
                    dialog.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (remark.getText().toString().isEmpty()) {
                                remark.setError("Remark required");
                                Toast.makeText(context, "Enter Remark", Toast.LENGTH_SHORT).show();
                            } else {
                                removeTruck(dialog, getAdapterPosition(), String.valueOf(modelLOT.getBookingid()), remark.getText().toString());
                            }
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


            itemLotBinding.expense.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayExpense(context, String.valueOf(modelLOT.getId()));
                }
            });
            itemLotBinding.note.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelLOT.getNote() == null || modelLOT.getNote().isEmpty()) {
                        Alert(context, "No Data Available");
                    } else {
                        Alert(context, modelLOT.getNote());
                    }
                }
            });

            Log.d(TAG, "bindDATA: " + modelLOT.getBookingid());
            Log.d(TAG, "bindDATA: " + modelLOT.getBidcount());
            Log.d(TAG, "bindDATA: " + modelLOT.getBiddingtype());
            Log.d(TAG, "bindDATA: " + modelLOT.getAcceptedbycorporate());

            if (modelLOT.getWeight().isEmpty() || modelLOT.getWeight() == null) {
                itemLotBinding.weight.setText("No Data");
            } else {
                itemLotBinding.weight.setText(modelLOT.getWeight() + "Ton");
            }

            if (modelLOT.getBidcount() == 0) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#F44336"));
                itemLotBinding.bid.setFocusable(false);
                itemLotBinding.bid.setClickable(false);

                itemLotBinding.accept.setFocusable(false);
                itemLotBinding.accept.setClickable(false);

                itemLotBinding.Amount.setFocusable(false);
                itemLotBinding.Amount.setClickable(false);

            } else if (modelLOT.getBidcount() >= 2) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#40E0D0"));
            }


            if (modelLOT.getAcceptedbycorporate() == 1) {
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#FFFFE974"));
            }
            if (modelLOT.getBiddingtype() == 2) {
                itemLotBinding.bid.setVisibility(View.GONE);
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#E66AA2"));
            }
//40E0D0
            itemLotBinding.Amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BidHistory(progress, context).execute(String.valueOf(modelLOT.getBookingid()), String.valueOf(modelLOT.getCorporateid()));
                }
            });

            itemLotBinding.paymentmode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + itemLotBinding.paymentmode);
                    if (modelLOT.getPaymentmode().equals("1")) {
                        String msg = "Advance : " + modelLOT.getAdvance() + " %" + "\n" +
                                "Balance : " + modelLOT.getBalance();
                        AlertAutoLink(context, msg, "Payments Details");
                    } else if (modelLOT.getPaymentmode().equals("4")) {
                        String msg = "Advance : " + modelLOT.getAdvance() + " %" + "\n" +
                                "Balance : " + modelLOT.getBalance() + "\n" +
                                "No Of Days : " + modelLOT.getNoofdays();
                        AlertAutoLink(context, msg, "Payments Details");
                    } else if (modelLOT.getPaymentmode().equals("3")) {
                        String msg =
                                "No Of Days : " + modelLOT.getNoofdays();
                        AlertAutoLink(context, msg, "Payments Details");
                    }
                }
            });

            itemLotBinding.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    args.putSerializable("itemBooking", modelLOT);
                    Frg_Share newFragment = new Frg_Share();
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    newFragment.setArguments(args);
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
/*
                    newFragment.setOnCallbackResult(new AnswerFragment.CallbackResult() {
                        @Override
                        public void sendResult(int requestCode, Object obj) {
                            if (requestCode == 9003) {
                            }
                        }
                    });
*/
                }
            });

            itemLotBinding.checkList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetChecklist(progress, context).execute(String.valueOf(modelLOT.getId()));
                }
            });

            itemLotBinding.corporateName.setOnClickListener(new View.OnClickListener() {
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

            itemLotBinding.bid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBIDDialoge(modelLOT);
                }
            });

            itemLotBinding.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BidDetails(progress, modelLOT, context, 1, itemLotBinding.bookingID).execute(String.valueOf(modelLOT.getBookingid()), String.valueOf(modelLOT.getCorporateid()));
                }
            });


        }
    }

    public static void displayExpense(Context context, String id) {
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

                            TextView TruckNo,TruckTyre,truckType,materialTextView;
                            TruckNo = dialog.findViewById(R.id.TruckNo);
                            TruckTyre = dialog.findViewById(R.id.TruckTyre);
                            truckType = dialog.findViewById(R.id.truckType);
                            materialTextView = dialog.findViewById(R.id.materialTextView);

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
                                    ListHolder bidHolder = (ListHolder)holder;
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
    private void removeTruck(Dialog dialog,int position, String bookingId, String Remark) {
        progress.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);
        StringEntity entity = null;
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("userid", USERINFO.getId());
            jsonParams.put("bookingid",bookingId);
            jsonParams.put("reason",Remark);
            jsonParams.put("createdby","0");
            entity = new StringEntity(jsonParams.toString());

            Log.d(TAG, "getTrucks: "+jsonParams.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.post(context,BookingReasonInsert,entity,"application/json" , new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                progress.dismiss();
                String result = new String(responseBody);
                Log.d(TAG, "onSuccess: "+result);
                Object json = null;
                if (result.equals("\"success\"")) {
                    dialog.dismiss();
                    Toast.makeText(context, "Booking Removed Successfully", Toast.LENGTH_SHORT).show();
                    modelLOTS.remove(position);
                    notifyDataSetChanged();
                } else {
                    Alert(context, "Some thing went wrong..");
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
