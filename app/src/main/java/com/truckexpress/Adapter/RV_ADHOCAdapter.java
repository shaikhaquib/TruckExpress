package com.truckexpress.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.truckexpress.Activity.BookingActivity;
import com.truckexpress.Extras.Constants;
import com.truckexpress.Extras.Progress;
import com.truckexpress.Models.ModelBidDetails;
import com.truckexpress.Models.ModelCurrentBooking;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.truckexpress.Activity.SplashScreen.USERINFO;
import static com.truckexpress.Extras.Constants.Alert;
import static com.truckexpress.Extras.Constants.AlertAutoLink;
import static com.truckexpress.Extras.Constants.CONNECTION_TIMEOUT;
import static com.truckexpress.Extras.Constants.READ_TIMEOUT;
import static com.truckexpress.Network.API.Bid_Acceptedbytranspoter;
import static com.truckexpress.Network.API.CHECKLIST;
import static com.truckexpress.Network.API.CORPORATEBID;
import static com.truckexpress.Network.API.SAVEBID;

public class RV_ADHOCAdapter extends RecyclerView.Adapter<RV_ADHOCAdapter.ViewHolder> {

    List<ModelLOT> modelLOTS;
    Context context;
    Progress progress;
    private static final String TAG = "RV_ADHOCAdapter";



    public RV_ADHOCAdapter(Context context, List<ModelLOT> modelLOTS) {
        this.modelLOTS = modelLOTS;
        this.context = context;
        progress = new Progress(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder viewHolder =  new ViewHolder((ItemAdhocBinding) DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_adhoc,parent,false));
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

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemAdhocBinding itemLotBinding;
        public ViewHolder(ItemAdhocBinding binding) {
            super(binding.getRoot());
            this.itemLotBinding = binding;
        }
        private void bindDATA(final ModelLOT modelLOT) {
            itemLotBinding.bookingID.setText("Booking ID :"+modelLOT.getId());
            itemLotBinding.corporateName.setText(Constants.capitalize(modelLOT.getCompanyName()));
            itemLotBinding.pickUPdate.setText(modelLOT.getPickupdate());

            if (modelLOT.getWeight().isEmpty() || modelLOT.getWeight()==null)
            {
                itemLotBinding.weight.setText("No Data");
            }
            else {
                itemLotBinding.weight.setText(modelLOT.getWeight() + "Ton");
            }

            itemLotBinding.Amount.setText(modelLOT.getRate()+ " " + modelLOT.getUnitid());
            itemLotBinding.trckType.setText(modelLOT.getTrucktype() +"\n/"+ modelLOT.getTyre() + " tyre");
            itemLotBinding.source.setText(modelLOT.getSource());
            itemLotBinding.destination.setText(modelLOT.getDestination());
            itemLotBinding.pickuplocation.setText(modelLOT.getPickupaddress());
            itemLotBinding.dropLocation.setText(modelLOT.getDropaddress());

            itemLotBinding.goodsType.setText(modelLOT.getGoodstype());
            itemLotBinding.paymentmode.setText(modelLOT.getPaymentname());
            itemLotBinding.totalfreight.setText(modelLOT.getTotalfreight());
            itemLotBinding.expense.setText("₹ "+modelLOT.getTotalexpenses());
            itemLotBinding.noofTruck.setText(String.valueOf("Number : "+modelLOT.getBookingnooftruck()));
            itemLotBinding.checkList.setText("Checklist : "+String.valueOf(modelLOT.getChecklistcount()));

            if (modelLOT.getAcceptedbycorporate() == 1){
                itemLotBinding.bookingID.setBackgroundColor(Color.parseColor("#00C853"));
            }

            itemLotBinding.paymentmode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: "+itemLotBinding.paymentmode);
                    if (modelLOT.getPaymentmode().equals("2")){
                        String msg = "Advance : "+modelLOT.getAdvance()+ " %"+"\n"+
                                "Balance : "+modelLOT.getBalance();
                        AlertAutoLink(context,msg,"Payments Details");
                    } else if (modelLOT.getPaymentmode().equals("4")) {
                        String msg = "Advance : "+modelLOT.getAdvance()+ " %"+"\n"+
                                "Balance : "+modelLOT.getBalance()+"\n"+
                                "No Of Days : "+modelLOT.getNoofdays();
                        AlertAutoLink(context,msg,"Payments Details");
                    } else if (modelLOT.getPaymentmode().equals("3")) {
                        String msg =
                                "No Of Days : "+modelLOT.getNoofdays();
                        AlertAutoLink(context,msg,"Payments Details");
                    }
                }
            });

            itemLotBinding.checkList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetChecklist(progress,context).execute(String.valueOf(modelLOT.getId()));
                }
            });

            itemLotBinding.corporateName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String msg = "Name : "+modelLOT.getName()+"\n"+
                            "Email ID : "+modelLOT.getCorporateContactPersoneEmail()+"\n"+
                            "Mobile No : "+modelLOT.getCorporateContactPerson();
                    final SpannableString s = new SpannableString(msg); // msg should have url to enable clicking
                    Linkify.addLinks(s, Linkify.ALL);

                    AlertAutoLink(context,s.toString(),"Corporate Details");
                }
            });

            itemLotBinding.Showmore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        itemLotBinding.viewMore.setVisibility(View.VISIBLE);
                        itemLotBinding.Showmore.setText("Show Less");
                    }else {
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
                    new BidDetails(progress,modelLOT).execute(String.valueOf(modelLOT.getBookingid()),String.valueOf(modelLOT.getCorporateid()));
                }
            });
        }

    }

    private void acceptBIDDialoge(final ModelBidDetails modelLOT, final ModelLOT lot) {
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
        bidBinding.noofTruck.setText(String.valueOf("Number : "+modelLOT.getNooftrucks()));
        bidBinding.rate.setText(String.valueOf(modelLOT.getRate()).trim());
        bidBinding.unit.setText(""+modelLOT.getUnitname());

        bidBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               new AcceptBiD(progress,modelLOT).execute(String.valueOf(lot.getTransporterid()));
            }
        });
    }

    private void addBIDDialoge(final ModelLOT modelLOT) {
        final AddBidBinding bidBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout. add_bid, null, false);

        final Dialog dialog  = new Dialog(context);
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

        bidBinding.bookingID.setText("Booking ID :"+modelLOT.getId());
        bidBinding.noofTruck.setText(String.valueOf("Number : "+modelLOT.getBookingnooftruck()));
        bidBinding.trckType.setText(modelLOT.getTrucktype() +" / "+ modelLOT.getTyre() + "tyre");
        bidBinding.rate.setText(String.valueOf(modelLOT.getRate()).trim());
        bidBinding.unit.setText(modelLOT.getUnitid());

        bidBinding.saveBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bidBinding.rate.getText().toString().isEmpty()){
                    bidBinding.rate.setError("Field Required...");
                    Alert(context,"Please set Rate");
                }else if (bidBinding.edtNoofTruck.getText().toString().isEmpty()){
                    bidBinding.edtNoofTruck.setError("Field Required...");
                Alert(context,"Please set Number of Truck");
            }else {
                    new SaveBID(modelLOT, dialog,progress).execute(bidBinding.rate.getText().toString(),bidBinding.edtNoofTruck.getText().toString());
                }
            }
        });
    }

    public class SaveBID extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        ModelLOT modelLOT;
        Dialog dialog;
        Progress progress;

        public SaveBID(ModelLOT modelLOT, Dialog dialog,Progress progress) {
            this.modelLOT = modelLOT;
            this.dialog = dialog;
            this.progress = progress;
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

            if (s.equals("\"success\"")) {
                dialog.dismiss();
                Alert(context, "We have registered your BID");
            } else {
                Alert(context, "Some thing went wrong..");
            }

        }
    }
    public class BidDetails extends AsyncTask<String , Void ,String> {
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        ModelLOT modelLOT;
        Dialog dialog;
        Progress progress;

        public BidDetails(Progress progress, ModelLOT modelLOT) {
            this.progress = progress;
            this.modelLOT = modelLOT;
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


            if (!s.equals("unsuccessful")) {


                Log.d(TAG, "onResponse: " + s.toString());

                Object json = null;
                try {
                    json = new JSONTokener(s).nextValue();

                    if (json instanceof JSONArray) {

                        JSONArray jsonArray = new JSONArray(s);

                        try {
                            JSONObject object = jsonArray.getJSONObject(0);
                            Gson gson = new Gson();
                            ModelBidDetails bidDetails = gson.fromJson(object.toString(), ModelBidDetails.class);
                            acceptBIDDialoge(bidDetails,modelLOT);

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

    public static void showChecklist(List<String> strings , Context context) {
        if (strings.size()>0) {
            String[] checkList = new String[strings.size()];
            checkList = strings.toArray(checkList);
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Checklist")
                    .setItems(checkList, null)
                    .setPositiveButton("Ok",null).show();
        }
    }

    private class AcceptBiD extends AsyncTask<String , Void ,String>{
        String server_response;
        String Json;
        HttpURLConnection conn;
        URL url = null;
        ModelBidDetails modelLOT;
        Dialog dialog;
        Progress progress;

        public AcceptBiD(Progress progress,ModelBidDetails modelBidDetails) {
            this.modelLOT = modelBidDetails;
            this.progress = progress;
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

            if (s.equals("\"success\"")) {
                Alert(context, "We have registered your request");
            } else {
                Alert(context, "Some thing went wrong..");
            }

        }
    }
}
